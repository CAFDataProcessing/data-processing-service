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
package com.github.cafdataprocessing.utilities.initialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.classification.service.client.ApiException;
import com.github.cafdataprocessing.classification.service.creation.WorkflowCreator;
import com.github.cafdataprocessing.classification.service.creation.created.CreationResult;
import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Utility class used to initialize Classification using base data files.
 */
public class ClassificationInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger(ClassificationInitializer.class);
    private static CreationResult initializeClassificationBaseData(String classificationApiUrl,
                                                                   String classificationBaseDataInputFile,
                                                                   String projectId,
                                                                   boolean overwriteExisting){
        WorkflowCreator classificationWorkflowCreator = new WorkflowCreator(classificationApiUrl);
        CreationResult creationResult;
        try {
            LOG.info("Initializing classification workflow base data.");
            creationResult = classificationWorkflowCreator.createWorkflowFromFile(classificationBaseDataInputFile,
                                                                                  projectId, overwriteExisting);
            LOG.info("Initialized classification workflow base data.");
        } catch (IOException | ApiException e) {
            throw new RuntimeException("Failure trying to create classification workflow.", e);
        }
        return creationResult;
    }

    private static void outputClassificationWorkflowCreationResult(
            CreationResult creationResult,
            String outputFilename){
        File outputFile = new File(outputFilename);
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileUtils.writeStringToFile(outputFile, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(creationResult));
        } catch (IOException e) {
            LOG.warn("Failed to output information of created classification workflow items.", e);
        }
    }

    private static CreationResult loadClassificationWorkflowOutputFile(String classificationBaseDataOutputFile){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(classificationBaseDataOutputFile),
                                    CreationResult.class);
        } catch (IOException e) {
            LOG.warn("Failed to read the output information of created classification workflow.", e);
        }
        return null;
    }

    /**
     * Creates a classification workflow using the provided input file if configured and returns a ClassificationWorkflowNameResolver for use in matching
     * Classification Workflow names to IDs. If any existing classification workflow is found with the same name as the workflow to create
     * then it will be removed before the workflow is created.
     * @param classificationApiUrl URL of the classification API to contact. Used in workflow creation and by name resolver.
     * @param createClassificationBaseData Whether a classification workflow should be created.
     * @param classificationBaseDataInputFile If {@code createClassificationBaseData} is true then the file contents of the location specified by this parameter
     *                                        are read and used to create a classification workflow.
     * @param classificationBaseDataOutputFile Destination file to output details of created classification workflow to.
     *                                         Pass null if no output file required.
     * @param projectId ProjectId to create classification workflow under.
     * @return A name resolver for resolving classification workflow names to IDs.
     */
    public static ClassificationWorkflowNameResolver initializeClassificationWorkflowIfRequired(String classificationApiUrl,
                                                                                                boolean createClassificationBaseData,
                                                                                                String classificationBaseDataInputFile,
                                                                                                String classificationBaseDataOutputFile,
                                                                                                String projectId){
        return initializeClassificationWorkflowIfRequired(classificationApiUrl,
                createClassificationBaseData,
                classificationBaseDataInputFile,
                classificationBaseDataOutputFile,
                projectId,
                true);
    }

    /**
     * Creates a classification workflow using the provided input file if configured and returns a ClassificationWorkflowNameResolver for use in matching
     * Classification Workflow names to IDs.
     * @param classificationApiUrl URL of the classification API to contact. Used in workflow creation and by name resolver.
     * @param createClassificationBaseData Whether a classification workflow should be created.
     * @param classificationBaseDataInputFile If {@code createClassificationBaseData} is true then the file contents of the location specified by this parameter
     *                                        are read and used to create a classification workflow.
     * @param classificationBaseDataOutputFile Destination file to output details of created classification workflow to.
     *                                         Pass null if no output file required.
     * @param projectId ProjectId to create classification workflow under.
     * @param overwriteExisting Whether any existing workflows that match the name of the workflow specified in the input file should be removed
     *                          before creation.
     * @return A name resolver for resolving classification workflow names to IDs.
     */
    public static ClassificationWorkflowNameResolver initializeClassificationWorkflowIfRequired(String classificationApiUrl,
                                                                                                boolean createClassificationBaseData,
                                                                                                String classificationBaseDataInputFile,
                                                                                                String classificationBaseDataOutputFile,
                                                                                                String projectId,
                                                                                                boolean overwriteExisting){
        ClassificationWorkflowNameResolver nameResolver;
        if(Strings.isNullOrEmpty(classificationApiUrl)){
            LOG.warn("No classification API URL property specified. Will be unable to resolve external classification workflow names to IDs unless they are present in base data output file.");
            nameResolver = new ClassificationWorkflowNameResolver(null);
        }
        else {
            com.github.cafdataprocessing.classification.service.client.ApiClient apiClient =
                    new com.github.cafdataprocessing.classification.service.client.ApiClient();
            apiClient.setBasePath(classificationApiUrl);
            nameResolver = new ClassificationWorkflowNameResolver(apiClient);
            if (createClassificationBaseData) {
                com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult =
                        initializeClassificationBaseData(classificationApiUrl,
                                classificationBaseDataInputFile,
                                projectId,
                                overwriteExisting);
                nameResolver.populateFromCreationResult(creationResult);
                if (!Strings.isNullOrEmpty(classificationBaseDataOutputFile)) {
                    outputClassificationWorkflowCreationResult(creationResult, classificationBaseDataOutputFile);
                }
            }
        }
        if(!Strings.isNullOrEmpty(classificationBaseDataOutputFile)){
            com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult =
                    loadClassificationWorkflowOutputFile(classificationBaseDataOutputFile);
            nameResolver.populateFromCreationResult(creationResult);
        }
        return nameResolver;
    }
}
