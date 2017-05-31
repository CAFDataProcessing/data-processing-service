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
package com.github.cafdataprocessing.utilities.tasksubmitter.environment;

import com.google.common.base.Strings;
import com.hpe.caf.boilerplate.webcaller.ApiClient;
import com.hpe.caf.boilerplate.webcaller.ApiException;
import com.hpe.caf.boilerplate.webcaller.api.BoilerplateApi;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.sun.jersey.api.client.ClientHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Verification functionality for properties relating to boilerplate expressions and their usage in actions in processing
 * workflows.
 */
public class ValidateBoilerplateProperties extends AbstractValidateApi{
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateBoilerplateProperties.class);

    /**
     * Validate that properties specified are suitable for the application's boilerplate expression needs.
     * @param properties Properties set for the application.
     */
    public static void validate(TaskSubmitterProperties properties){
        boolean createBoilerplateBaseData = properties.getCreateBoilerplateBaseData();
        if(createBoilerplateBaseData){
            LOGGER.info(TaskSubmitterProperties.PropertyNames.BaseData.CREATE_BOILERPLATE_BASE_DATA+
                    " is set to 'true', expecting boilerplate base data properties to be set.");
            validateBoilerplateBaseDataProperties(properties);
        }
    }

    private static void validateBoilerplateBaseDataProperties(TaskSubmitterProperties properties){
        String boilerplateBaseDataInputFileStr = properties.getBoilerplateBaseDataInputFile();
        if(Strings.isNullOrEmpty(boilerplateBaseDataInputFileStr)) {
            throw new RuntimeException("Property "
                    +TaskSubmitterProperties.PropertyNames.BaseData.BOILERPLATE_BASE_DATA_INPUT_FILE+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_BOILERPLATE_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }

        File boilerplateBaseDataInputFile = new File(boilerplateBaseDataInputFileStr);
        if(!boilerplateBaseDataInputFile.exists() || boilerplateBaseDataInputFile.isDirectory()){
            throw new RuntimeException("The file specified for "+
                    TaskSubmitterProperties.PropertyNames.BaseData.BOILERPLATE_BASE_DATA_INPUT_FILE
                    +" could not be found.");
        }

        String boilerplateBaseDataOutputFileStr = properties.getBoilerplateBaseDataOutputFile();
        if(Strings.isNullOrEmpty(boilerplateBaseDataOutputFileStr)) {
            throw new RuntimeException("Property "
                    +TaskSubmitterProperties.PropertyNames.BaseData.BOILERPLATE_BASE_DATA_OUTPUT_FILE+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_BOILERPLATE_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }

        String boilerplateApiUrl = properties.getBoilerplateApiUrl();
        if(Strings.isNullOrEmpty(boilerplateApiUrl)){
            throw new RuntimeException("Property "+
                    TaskSubmitterProperties.PropertyNames.BaseData.BOILERPLATE_API_URL+
                    " must be set if "
                    +TaskSubmitterProperties.PropertyNames.BaseData.CREATE_BOILERPLATE_BASE_DATA+
                    " is set to 'true'. Currently has no value.");
        }

        if(!waitUntilBoilerplateApiReady(boilerplateApiUrl, properties.getExternalApiRetryAttempts())){
            throw new RuntimeException("Boilerplate API is not ready to handle requests. Verify your boilerplate API is configured correctly.");
        }
    }

    private static boolean waitUntilBoilerplateApiReady(String boilerplateApiUrl, Integer boilerplateApiReadyRetryAttempts){
        ApiClient boilerplateApiClient = new ApiClient();
        boilerplateApiClient.setApiKey("Ignored");
        boilerplateApiClient.setBasePath(boilerplateApiUrl);
        BoilerplateApi boilerplateApi = new BoilerplateApi(boilerplateApiClient);

        int retryCount = 0;
        boolean apiReady = false;
        while(boilerplateApiReadyRetryAttempts == -1 || retryCount < boilerplateApiReadyRetryAttempts) {
            try {
                LOGGER.info("Sending example request to boilerplate API to verify it responds as expected.");
                boilerplateApi.getTags(1, 1);
                //if exception was not thrown then indicates successful communication and ready to proceed
                LOGGER.info("The boilerplate API replied with a valid response. Boilerplate API ready to receive requests.");
                apiReady = true;
                break;
            } catch (ApiException | ClientHandlerException e) {
                LOGGER.warn("The boilerplate API request failed while testing ability to communicate with API. Will retry after a delay.", e);
            }
            delayCall("Thread interrupted while waiting before retrying boilerplate API health check.");
            retryCount++;
        }
        return apiReady;
    }
}
