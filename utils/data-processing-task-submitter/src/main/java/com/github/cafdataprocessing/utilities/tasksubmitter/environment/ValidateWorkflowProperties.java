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
package com.github.cafdataprocessing.utilities.tasksubmitter.environment;

import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionTypesApi;
import com.github.cafdataprocessing.processing.service.client.api.AdminApi;
import com.github.cafdataprocessing.processing.service.client.model.HealthStatus;
import com.google.common.base.Strings;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.sun.jersey.api.client.ClientHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Verification functionality for processing workflow properties read in.
 */
public class ValidateWorkflowProperties extends AbstractValidateApi{
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateWorkflowProperties.class);

    /**
     * Validate that properties specified are suitable for the application's processing workflow needs.
     * @param properties Properties set for the application.
     */
    public static void validate(TaskSubmitterProperties properties){
        //check if workflow ID is provided and valid
        Long workflowId;
        try {
            workflowId = properties.getWorkflowId();
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("Unable to convert property "+
                    TaskSubmitterProperties.PropertyNames.WORKFLOW_ID
                    +" to a valid number.");
        }
        if(workflowId==null) {
            LOGGER.info("No workflow ID provided, expecting workflow base data properties to be set.");
            validateWorkflowBaseDataProperties(properties);
        }
    }

    public static void validateWorkflowBaseDataProperties(TaskSubmitterProperties properties){
        String workflowBaseDataFileStr = properties.getWorkflowBaseDataFile();
        if(Strings.isNullOrEmpty(workflowBaseDataFileStr)) {
            throw new RuntimeException("Property "
                    +TaskSubmitterProperties.PropertyNames.BaseData.WORKFLOW_BASE_DATA_FILE+
                    " must be set if no workflow ID is specified. Currently has no value.");
        }

        File workflowBaseDataFile = new File(workflowBaseDataFileStr);
        if(!workflowBaseDataFile.exists() || workflowBaseDataFile.isDirectory()){
            throw new RuntimeException("The file specified for "+
                    TaskSubmitterProperties.PropertyNames.BaseData.WORKFLOW_BASE_DATA_FILE
                    +" could not be found.");
        }

        String processingApiUrl = properties.getProcessingApiUrl();
        if(Strings.isNullOrEmpty(processingApiUrl)){
            throw new RuntimeException("Property "+
                    TaskSubmitterProperties.PropertyNames.BaseData.PROCESSING_API_URL+
                    " must be set if no workflow ID is specified. Currently has no value.");
        }

        if(!waitUntilProcessingApiReady(processingApiUrl, properties.getExternalApiRetryAttempts())){
            throw new RuntimeException("Processing API is not ready to handle requests. Verify your processing API is configured correctly.");
        }
    }

    private static boolean waitUntilProcessingApiReady(String processingApiUrl, int processingApiRetryAttempts){
        ApiClient workflowApiClient = new ApiClient();
        workflowApiClient.setBasePath(processingApiUrl);
        performHealthCheck(processingApiRetryAttempts, workflowApiClient, processingApiUrl);

        //with basic heath validated now check that database communication is established in the API
        return performExampleRequest(processingApiRetryAttempts, workflowApiClient);
    }

    private static void performHealthCheck(int apiRetryAttempts, ApiClient workflowApiClient, String processingApiUrl){
        AdminApi adminApi = new AdminApi(workflowApiClient);
        HealthStatus healthStatus;
        int retryCount = 0;
        while(apiRetryAttempts == -1 || retryCount < apiRetryAttempts) {
            try {
                healthStatus = adminApi.healthCheck();
                if (healthStatus.getStatus() != HealthStatus.StatusEnum.HEALTHY) {
                    LOGGER.warn("The processing API is reporting as unhealthy. Unable to proceed.");
                }
                else{
                    LOGGER.info("The processing API is reporting as healthy.");
                    break;
                }
            } catch (ApiException | ClientHandlerException e) {
                LOGGER.warn("Failure attempting to contact Processing API with "
                        + TaskSubmitterProperties.PropertyNames.BaseData.PROCESSING_API_URL +
                        " property value: " + processingApiUrl, e);
            }
            delayCall("Thread interrupted while waiting before retrying processing API health check.");
            retryCount++;
        }
    }

    private static boolean performExampleRequest(int apiRetryAttempts, ApiClient workflowApiClient){
        ActionTypesApi actionTypesApi = new ActionTypesApi(workflowApiClient);
        int retryCount = 0;
        boolean processingApiReady = false;
        while(apiRetryAttempts == -1 || retryCount < apiRetryAttempts) {
            try {
                LOGGER.info("Sending example request to processing API to verify it responds as expected.");
                actionTypesApi.getActionTypes("Ignored", 1, 1);
                //if exception was not thrown then indicates successful communication and ready to proceed
                LOGGER.info("The processing API replied with a valid response. Processing API ready to receive requests.");
                processingApiReady = true;
                break;
            } catch (ApiException | ClientHandlerException e) {
                LOGGER.warn("The processing API request failed while testing ability to communicate with API. Will retry after a delay.", e);
            }
            delayCall("Thread interrupted while waiting before retrying processing API request.");
            retryCount++;
        }
        return processingApiReady;
    }
}
