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
package com.github.cafdataprocessing.utilities.tasksubmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.classification.service.creation.WorkflowCreator;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.utilities.tasksubmitter.environment.ValidateEnvironment;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.ActionTypeNameResolver;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.ClassificationWorkflowNameResolver;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.WorkflowInitializer;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.boilerplate.BoilerplateInvoker;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.boilerplate.BoilerplateNameResolver;
import com.github.cafdataprocessing.utilities.tasksubmitter.initialize.boilerplate.CreationResultJson;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.github.cafdataprocessing.utilities.tasksubmitter.services.Services;
import com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage.TaskMessagePublisher;
import com.google.common.base.Strings;
import com.hpe.caf.boilerplate.webcaller.ApiClient;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for task submitter application, reading in arguments and performing application function
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException, ApiException {
        (new Main()).run();
    }

    public void run() throws IOException, InterruptedException, ApiException {
        //check that this is a valid environment to run application in
        ValidateEnvironment.validate();

        TaskSubmitterProperties properties = Services.getInstance().getTaskSubmitterProperties();
        String projectId = properties.getProjectId();

        String documentInputDirectory = properties.getDocumentInputDirectory();
        Long workflowId = properties.getWorkflowId();

        if(workflowId==null){
            LOGGER.info("No workflow ID passed. Workflow will be created and used in published task messages.");
            //create boilerplate expressions/tags if applicable and set up the boilerplate name resolver
            BoilerplateNameResolver boilerplateNameResolver = initializeBoilerplateIfRequired(properties);

            //create classification workflow if applicable and set up the classification name resolver
            ClassificationWorkflowNameResolver classificationWorkflowNameResolver =
                    initializeClassificationWorkflowIfRequired(properties);

            WorkflowInitializer workflowInitializer = new WorkflowInitializer(properties.getProcessingApiUrl(),
                    boilerplateNameResolver, new ActionTypeNameResolver(), classificationWorkflowNameResolver);
            workflowId = workflowInitializer.initializeWorkflowBaseData(properties.getWorkflowBaseDataFile(),
                    properties.getProjectId());
            LOGGER.info("Created workflow that will be sent in task messages has ID: "+workflowId);
        }

        ExecutorService monitorService = TaskMessagePublisher.publishTaskMessagesForDirectoryAndMonitor(documentInputDirectory,
                workflowId, projectId, properties.getSentDocumentsDirectory());
        LOGGER.info("Monitoring directory for new documents.");
        monitorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private static void initializeBoilerplateBaseData(TaskSubmitterProperties properties){
        BoilerplateInvoker bpInvoker = new BoilerplateInvoker(
                properties.getBoilerplateBaseDataInputFile(),
                properties.getBoilerplateBaseDataOutputFile(),
                properties.getBoilerplateApiUrl()
        );
        bpInvoker.run();
    }

    private static CreationResultJson loadBoilerplateOutputFile(TaskSubmitterProperties properties){
        //read in the output data of the created expressions
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(properties.getBoilerplateBaseDataOutputFile()), CreationResultJson.class);
        } catch (IOException e) {
            LOGGER.warn("Failed to read the output information of created boilerplate expressions and tags.", e);
        }
        return null;
    }

    private static com.github.cafdataprocessing.classification.service.creation.created.CreationResult initializeClassificationBaseData(TaskSubmitterProperties properties){
        WorkflowCreator classificationWorkflowCreator = new WorkflowCreator(properties.getClassificationApiUrl());
        com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult = null;
        try {
            LOGGER.info("Initializing classification workflow base data.");
            creationResult = classificationWorkflowCreator.createWorkflowFromFile(properties.getClassificationBaseDataInputFile(),
                    properties.getProjectId());
            LOGGER.info("Initialized classification workflow base data.");
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
            LOGGER.warn("Failed to output information of created classification workflow items.", e);
        }
    }

    public static com.github.cafdataprocessing.classification.service.creation.created.CreationResult loadClassificationWorkflowOutputFile(
            TaskSubmitterProperties properties
    ){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(properties.getClassificationBaseDataOutputFile()),
                    com.github.cafdataprocessing.classification.service.creation.created.CreationResult.class);
        } catch (IOException e) {
            LOGGER.warn("Failed to read the output information of created classification workflow.", e);
        }
        return null;
    }

    private static BoilerplateNameResolver initializeBoilerplateIfRequired(TaskSubmitterProperties properties){
        BoilerplateNameResolver nameResolver;
        if(Strings.isNullOrEmpty(properties.getBoilerplateApiUrl())){
            LOGGER.warn("No boilerplate API URL property specified. Will be unable to resolve expression and tag names to IDs unless they are present in base data output file.");
            nameResolver = new BoilerplateNameResolver(null);
        }
        else {
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKey(properties.getProjectId());
            apiClient.setBasePath(properties.getBoilerplateApiUrl());
            nameResolver = new BoilerplateNameResolver(apiClient);
            if (properties.getCreateBoilerplateBaseData()) {
                initializeBoilerplateBaseData(properties);
            }
        }
        if (!Strings.isNullOrEmpty(properties.getBoilerplateBaseDataOutputFile())) {
            CreationResultJson boilerplateCreationResult = loadBoilerplateOutputFile(properties);
            nameResolver.populateFromCreationResult(boilerplateCreationResult);
        }
        return nameResolver;
    }

    private static ClassificationWorkflowNameResolver initializeClassificationWorkflowIfRequired(
            TaskSubmitterProperties properties){
        ClassificationWorkflowNameResolver nameResolver;
        if(Strings.isNullOrEmpty(properties.getClassificationApiUrl())){
            LOGGER.warn("No classification API URL property specified. Will be unable to resolve external classification workflow names to IDs unless they are present in base data output file.");
            nameResolver = new ClassificationWorkflowNameResolver(null);
        }
        else {
            com.github.cafdataprocessing.classification.service.client.ApiClient apiClient =
                    new com.github.cafdataprocessing.classification.service.client.ApiClient();
            apiClient.setBasePath(properties.getClassificationApiUrl());
            nameResolver = new ClassificationWorkflowNameResolver(apiClient);
            if (properties.getCreateClassificationBaseData()) {
                com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult =
                        initializeClassificationBaseData(properties);
                nameResolver.populateFromCreationResult(creationResult);
                if (!Strings.isNullOrEmpty(properties.getClassificationBaseDataOutputFile())) {
                    outputClassificationWorkflowCreationResult(creationResult,
                            properties.getClassificationBaseDataOutputFile());
                }
            }
        }
        if(!Strings.isNullOrEmpty(properties.getClassificationBaseDataOutputFile())){
            com.github.cafdataprocessing.classification.service.creation.created.CreationResult creationResult =
                    loadClassificationWorkflowOutputFile(properties);
            nameResolver.populateFromCreationResult(creationResult);
        }
        return nameResolver;
    }
}
