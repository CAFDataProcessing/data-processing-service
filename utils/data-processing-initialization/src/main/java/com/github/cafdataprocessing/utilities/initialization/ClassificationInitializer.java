/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
import com.github.cafdataprocessing.classification.service.creation.WorkflowCreator;
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
    private static com.github.cafdataprocessing.classification.service.creation.created.CreationResult initializeClassificationBaseData(String classificationApiUrl, String classificationBaseDataInputFile, String projectId){
        WorkflowCreator classificationWorkflowCreator = new WorkflowCreator(classificationApiUrl);
        com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult = null;
        try {
            LOG.info("Initializing classification workflow base data.");
            creationResult = classificationWorkflowCreator.createWorkflowFromFile(classificationBaseDataInputFile,
                                                                                  projectId);
            LOG.info("Initialized classification workflow base data.");
        } catch (IOException | com.github.cafdataprocessing.classification.service.client.ApiException e) {
            throw new RuntimeException("Failure trying to create classification workflow.", e);
        }
        return creationResult;
    }

    private static void outputClassificationWorkflowCreationResult(
            com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult,
            String outputFilename){
        File outputFile = new File(outputFilename);
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileUtils.writeStringToFile(outputFile, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(creationResult));
        } catch (IOException e) {
            LOG.warn("Failed to output information of created classification workflow items.", e);
        }
    }

    private static com.github.cafdataprocessing.classification.service.creation.created.CreationResult loadClassificationWorkflowOutputFile(String classificationBaseDataOutputFile){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(classificationBaseDataOutputFile),
                                    com.github.cafdataprocessing.classification.service.creation.created.CreationResult.class);
        } catch (IOException e) {
            LOG.warn("Failed to read the output information of created classification workflow.", e);
        }
        return null;
    }

    public static ClassificationWorkflowNameResolver initializeClassificationWorkflowIfRequired(String classificationApiUrl, boolean createClassificationBaseData, String classificationBaseDataInputFile, String classificationBaseDataOutputFile, String projectId){
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
                        initializeClassificationBaseData(classificationApiUrl, classificationBaseDataInputFile, projectId);
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
