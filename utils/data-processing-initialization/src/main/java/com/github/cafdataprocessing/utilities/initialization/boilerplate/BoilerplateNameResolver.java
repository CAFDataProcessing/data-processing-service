/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cafdataprocessing.utilities.initialization.boilerplate;

import com.google.common.base.Strings;
import com.hpe.caf.boilerplate.webcaller.ApiClient;
import com.hpe.caf.boilerplate.webcaller.ApiException;
import com.hpe.caf.boilerplate.webcaller.api.BoilerplateApi;
import com.hpe.caf.boilerplate.webcaller.model.BoilerplateExpression;
import com.hpe.caf.boilerplate.webcaller.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Resolves boilerplate expression and tag names to their IDs
 */
public class BoilerplateNameResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoilerplateNameResolver.class);

    private Map<String, Long> expressionIds = new LinkedHashMap<>();
    private Map<String, Long> tagIds = new LinkedHashMap<>();

    private final BoilerplateApi boilerplateApi;

    /**
     * Creates new instance of BoilerplateNameResolver
     * @param apiClient ApiClient to use to create BoilerplateApi which will be contacted to resolve unknown names.
     */
    public BoilerplateNameResolver(ApiClient apiClient){
        if(apiClient!=null) {
            this.boilerplateApi = new BoilerplateApi(apiClient);
        }
        else {
            this.boilerplateApi = null;
        }
    }

    /**
     * Records the names and IDs from the provided creation result for use in later resolution.
     * @param boilerplateCreationResult Creation Result with boilerplate expression/tag names and IDs.
     */
    public void populateFromCreationResult(CreationResultJson boilerplateCreationResult){
        if(boilerplateCreationResult!=null) {
            Collection<CreationResultOutputJson> creationResultExpressions = boilerplateCreationResult.expressions;
            if (creationResultExpressions != null) {
                creationResultExpressions.stream()
                        .forEach(
                                ex -> expressionIds.put(ex.name, ex.id)
                        );
            }
            Collection<CreationResultOutputJson> creationResultTags = boilerplateCreationResult.tags;
            if(creationResultTags!=null) {
                creationResultTags.stream()
                        .forEach(
                                tag -> tagIds.put(tag.name, tag.id)
                        );
            }
        }
    }

    public void populateExpressionsFromApiCall(String projectId) throws ApiException
    {
        if(boilerplateApi==null){
            LOGGER.error("Boilerplate API is not configured for usage, unable to retrieve expressions.");
            return;
        }

        int pageIndex = 1;
        int pageSize = 100;
        int retrievedHitsSize;
        if (!Strings.isNullOrEmpty(projectId)) {
            boilerplateApi.getApiClient().setApiKey(projectId);
        }
        do {
            List<BoilerplateExpression> retrievedExpressions = boilerplateApi.getExpressions(pageIndex, pageSize);
            pageIndex = pageIndex + pageSize;
            retrievedHitsSize = retrievedExpressions.size();
            for(BoilerplateExpression retrievedExpression : retrievedExpressions){
                if(expressionIds.containsKey(retrievedExpression.getName())){
                    continue;
                }
                expressionIds.put(retrievedExpression.getName(), retrievedExpression.getId());
            }
        }
        while(retrievedHitsSize >= pageSize);
    }

    /**
     * Populates the known mapping of expression names to expression IDs by querying the boilerplate API for all existing expressions.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public void populateExpressionsFromApiCall() throws ApiException {
        populateExpressionsFromApiCall(null);
    }

    /**
     * Populates the known mapping of tag names to tag IDs by querying the boilerplate API for all existing tags.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public void populateTagsFromApiCall() throws ApiException {
        if(boilerplateApi==null){
            LOGGER.error("Boilerplate API is not configured for usage, unable to retrieve tags.");
            return;
        }

        int pageIndex = 1;
        int pageSize = 100;
        int retrievedHitsSize;
        do {
            List<Tag> retrievedTags = boilerplateApi.getTags(pageIndex, pageSize);
            retrievedHitsSize = retrievedTags.size();
            pageIndex = pageIndex + pageSize;
            for(Tag retrievedTag : retrievedTags){
                if(tagIds.containsKey(retrievedTag.getName())){
                    continue;
                }
                tagIds.put(retrievedTag.getName(), retrievedTag.getId());
            }
        }
        while(retrievedHitsSize >= pageSize);
    }

    /**
     * Returns the tag ID matching the provided name.
     * @param tagName Name to get ID for.
     * @return ID matching the passed name.
     */
    public Long resolveFromTagName(String tagName){
        Long matchedTagId = tagIds.get(tagName);
        if(matchedTagId!=null){
            return matchedTagId;
        }
        try {
            populateTagsFromApiCall();
        } catch (ApiException e) {
            LOGGER.error("Failure trying to retrieve tag information from boilerplate API.", e);
        }
        //having just populated with up to date tags from the API try and retrieve the tag ID again
        return tagIds.get(tagName);
    }

    /**
     * Returns the boilerplate expression ID matching the provided name.
     * @param expressionName Name to get ID for.
     * @return ID matching the passed name.
     */
    public Long resolveFromExpressionName(String expressionName){
        Long matchedExpId = expressionIds.get(expressionName);
        if(matchedExpId!=null){
            return matchedExpId;
        }
        try {
            populateExpressionsFromApiCall();
        } catch (ApiException e) {
            LOGGER.error("Failure trying to retrieve expression information from boilerplate API.", e);
        }
        //having just populated with up to date expressions from the API try and retrieve the expression ID again
        return expressionIds.get(expressionName);
    }

    /**
     * Takes in an array of expression IDs, with each ID being either the expression ID or the name of the expression, and returns
     * an array of resovled expression IDs.
     * @param expressionIdsObj Array of numeric IDs and expression names.
     * @return Array of numeric IDs with any names converted to their appropriate IDs.
     */
    public List<Object> resolveExpressionIds(ArrayList expressionIdsObj){
        if(expressionIdsObj==null) {
            return null;
        }
        List<Object> newExpressionIds = new ArrayList<>();

        ArrayList<Object> expressionIds = expressionIdsObj;
        for(Object expressionId: expressionIds){
            if(expressionId instanceof Integer || expressionId instanceof Long){
                newExpressionIds.add(expressionId);
            }
            else{
                //String value indicates this is the name of a boilerplate expression, try and retrieve the appropriate ID
                Long resolvedExpId = resolveFromExpressionName((String)expressionId);
                if(resolvedExpId==null){
                    throw new RuntimeException("Failure trying to resolve expression ID using value: "+expressionId);
                }
                newExpressionIds.add(resolvedExpId);
            }
        }
        return newExpressionIds;
    }

    /**
     * Takes an object which may be either a String (containing name of a tag) or an Integer/Long (containing ID of tag) and returns the appropriate tag ID.
     * @param tagId Possible tag name or ID.
     * @return Matching tag ID.
     */
    public Object resolveTagId(Object tagId){
        if(tagId==null){
            return null;
        }
        if(tagId instanceof Integer | tagId instanceof Long){
            return tagId;
        }
        //string value indicates this is the name of a tag, try and retrieve the appropriate ID
        Long resolvedTagId = resolveFromTagName((String) tagId);
        if(resolvedTagId==null){
            throw new RuntimeException("Failure trying to resolve tag ID using value: "+tagId);
        }
        return resolvedTagId;
    }
}
