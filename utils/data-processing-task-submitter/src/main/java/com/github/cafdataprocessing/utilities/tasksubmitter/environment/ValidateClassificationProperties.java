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

import com.github.cafdataprocessing.classification.service.client.ApiClient;
import com.github.cafdataprocessing.classification.service.client.ApiException;
import com.github.cafdataprocessing.classification.service.client.api.AdminApi;
import com.github.cafdataprocessing.classification.service.client.api.TermsApi;
import com.github.cafdataprocessing.classification.service.client.model.HealthStatus;
import com.google.common.base.Strings;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.sun.jersey.api.client.ClientHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Verification functionality for properties relating to classification workflows and their usage in actions in processing
 * workflows.
 */
public class ValidateClassificationProperties extends AbstractValidateApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateClassificationProperties.class);

    /**
     * Validate that properties specified are suitable for the application's classification workflow needs.
     * @param properties Properties set for the application.
     */
    public static void validate(TaskSubmitterProperties properties){
        boolean createClassificationBaseData = properties.getCreateClassificationBaseData();
        if(createClassificationBaseData){
            LOGGER.info(TaskSubmitterProperties.PropertyNames.BaseData.CREATE_CLASSIFICATION_BASE_DATA+
                    " is set to 'true', expecting classification base data properties to be set.");
            validateClassificationBaseDataProperties(properties);
        }
    }

    private static void validateClassificationBaseDataProperties(TaskSubmitterProperties properties){
        String classificationBaseDataInputFileStr = properties.getClassificationBaseDataInputFile();
        if(Strings.isNullOrEmpty(classificationBaseDataInputFileStr)){
            throw new RuntimeException("Property "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CLASSIFICATION_BASE_DATA_INPUT_FILE+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_CLASSIFICATION_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }
        File classificationBaseDataInputFile = new File(classificationBaseDataInputFileStr);
        if(!classificationBaseDataInputFile.exists() || classificationBaseDataInputFile.isDirectory()){
            throw new RuntimeException("The file specified for "+
                    TaskSubmitterProperties.PropertyNames.BaseData.CLASSIFICATION_BASE_DATA_INPUT_FILE
                    +" could not be found.");
        }

        String classificationBaseDataOutputFileStr = properties.getClassificationBaseDataOutputFile();
        if(Strings.isNullOrEmpty(classificationBaseDataOutputFileStr)) {
            throw new RuntimeException("Property "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CLASSIFICATION_BASE_DATA_OUTPUT_FILE+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_CLASSIFICATION_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }

        String classificationApiUrl = properties.getClassificationApiUrl();
        if(Strings.isNullOrEmpty(classificationApiUrl)){
            throw new RuntimeException("Property "+
                    TaskSubmitterProperties.PropertyNames.BaseData.CLASSIFICATION_API_URL+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_CLASSIFICATION_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }
        if(!waitUntilClassificationApiReady(classificationApiUrl, properties.getExternalApiRetryAttempts())){
            throw new RuntimeException("Classification API is not ready to handle requests. Verify your classification API is configured correctly.");
        }
    }

    private static boolean waitUntilClassificationApiReady(String classificationApiUrl, int apiRetryAttempts){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(classificationApiUrl);
        performHealthCheck(apiRetryAttempts, apiClient, classificationApiUrl);
        //with basic heath validated now check that database communication is established in the API
        return performExampleRequest(apiRetryAttempts, apiClient);
    }

    private static void performHealthCheck(int apiRetryAttempts, ApiClient apiClient, String classificationApiUrl){
        AdminApi adminApi =
                new AdminApi(apiClient);
        HealthStatus healthStatus;
        int retryCount = 0;
        while(apiRetryAttempts == -1 || retryCount < apiRetryAttempts) {
            try {
                healthStatus = adminApi.healthCheck();
                if (healthStatus.getStatus() !=  HealthStatus.StatusEnum.HEALTHY) {
                    LOGGER.warn("The classification API is reporting as unhealthy. Will retry after a delay.");
                }
                else{
                    LOGGER.info("The classification API is reporting as healthy.");
                    break;
                }
            } catch (ApiException | ClientHandlerException e) {
                LOGGER.warn("Failure attempting to contact classification API with "
                        + TaskSubmitterProperties.PropertyNames.BaseData.CLASSIFICATION_API_URL +
                        " property value: " + classificationApiUrl, e);
            }
            delayCall("Thread interrupted while waiting before retrying classification API health check.");
            retryCount++;
        }
    }

    private static boolean performExampleRequest(int apiRetryAttempts, ApiClient apiClient){
        TermsApi termsApi = new TermsApi(apiClient);
        int retryCount = 0;
        boolean classificationApiReady = false;
        while(apiRetryAttempts == -1 || retryCount < apiRetryAttempts) {
            try {
                LOGGER.info("Sending example request to classification API to verify it responds as expected.");
                termsApi.getTermLists("Ignored", 1, 1);
                //if exception was not thrown then indicates successful communication and ready to proceed
                LOGGER.info("The classification API replied with a valid response. Classification API ready to receive requests.");
                classificationApiReady = true;
                break;
            } catch (ApiException | ClientHandlerException e) {
                LOGGER.warn("The classification API request failed while testing ability to communicate with API. Will retry after a delay.", e);
            }
            delayCall("Thread interrupted while waiting before retrying classification API request.");
            retryCount++;
        }
        return classificationApiReady;
    }
}
