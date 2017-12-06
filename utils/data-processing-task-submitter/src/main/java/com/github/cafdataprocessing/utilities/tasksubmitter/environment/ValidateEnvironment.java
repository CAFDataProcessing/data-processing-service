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

import com.github.cafdataprocessing.utilities.queuehelper.RabbitProperties;
import com.github.cafdataprocessing.utilities.tasksubmitter.properties.TaskSubmitterProperties;
import com.github.cafdataprocessing.utilities.tasksubmitter.services.Services;
import com.google.common.base.Strings;

/**
 * Validates that the environment the application is running in is correct.
 */
public class ValidateEnvironment {

    /**
     * Validate that properties set in the environment are suitable for the application's needs.
     */
    public static void validate(){
        Services services = Services.getInstance();
        validateTaskSubmitterProperties(services.getTaskSubmitterProperties());
        validateRabbitProperties(services.getRabbitProperties());
    }

    private static void validateRabbitProperties(RabbitProperties properties){
        if (properties.getHost() == null) {
            throw new RuntimeException("Required environment property CAF_RABBITMQ_HOST must be set, currently has no value.");
        }
        if(Strings.isNullOrEmpty(properties.getPublishQueue())) {
            throw new RuntimeException("Required environment property CAF_RABBITMQ_PUBLISH_QUEUE must be set, currently has no value.");
        }
    }

    private static void validateTaskSubmitterProperties(TaskSubmitterProperties properties){
        //check that input directory provided
        String inputDir = properties.getDocumentInputDirectory();
        if(Strings.isNullOrEmpty(inputDir)){
            throw new RuntimeException("Environment property "+
                    TaskSubmitterProperties.PropertyNames.DOCUMENT_INPUT_DIRECTORY
                    +" must be set, currently has no value.");
        }

        //check that a project ID is provided
        String projectId = properties.getProjectId();
        if(Strings.isNullOrEmpty(projectId)){
            throw new RuntimeException("Environment property "+
                    TaskSubmitterProperties.PropertyNames.PROJECT_ID+
                    " must be set, currently has no value.");
        }

        ValidateWorkflowProperties.validate(properties);
        ValidateBoilerplateProperties.validate(properties);
    }
}
