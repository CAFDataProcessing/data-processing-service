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

import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.utilities.initialization.*;
import com.github.cafdataprocessing.utilities.tasksubmitter.environment.ValidateEnvironment;
import com.github.cafdataprocessing.utilities.initialization.boilerplate.BoilerplateNameResolver;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.github.cafdataprocessing.utilities.tasksubmitter.services.Services;
import com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage.TaskMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            BoilerplateNameResolver boilerplateNameResolver = BoilerplateInitializer.initializeBoilerplateIfRequired(properties.getBoilerplateApiUrl(), properties.getProjectId(), properties.getBoilerplateBaseDataInputFile(), properties.getBoilerplateBaseDataOutputFile(), properties.getCreateBoilerplateBaseData());

            //create classification workflow if applicable and set up the classification name resolver
            ClassificationWorkflowNameResolver classificationWorkflowNameResolver =
                    ClassificationInitializer.initializeClassificationWorkflowIfRequired(properties.getClassificationApiUrl(), properties.getCreateClassificationBaseData(), properties.getClassificationBaseDataInputFile(), properties.getClassificationBaseDataOutputFile(), projectId);

            WorkflowInitializer workflowInitializer = new WorkflowInitializer(properties.getProcessingApiUrl(), boilerplateNameResolver, new ActionTypeNameResolver(), classificationWorkflowNameResolver);
            workflowId = workflowInitializer.initializeWorkflowBaseData(properties.getWorkflowBaseDataFile(),
                    properties.getProjectId());
            LOGGER.info("Created workflow that will be sent in task messages has ID: "+workflowId);
        }

        ExecutorService monitorService = TaskMessagePublisher.publishTaskMessagesForDirectoryAndMonitor(documentInputDirectory,
                workflowId, projectId, properties.getSentDocumentsDirectory());
        LOGGER.info("Monitoring directory for new documents.");
        monitorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
}
