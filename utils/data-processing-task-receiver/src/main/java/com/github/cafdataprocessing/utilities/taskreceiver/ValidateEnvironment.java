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
package com.github.cafdataprocessing.utilities.taskreceiver;

import com.github.cafdataprocessing.utilities.queuehelper.RabbitProperties;
import com.github.cafdataprocessing.utilities.taskreceiver.properties.TaskReceiverProperties;
import com.github.cafdataprocessing.utilities.taskreceiver.services.Services;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Validates that the environment the application is running in is correct.
 */
public class ValidateEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateEnvironment.class);

    /**
     * Validates the application environment is valid to run in e.g. all required properties are specified.
     */
    public static void validate(){
        Services services = Services.getInstance();
        validateTaskReceiverProperties(services.getTaskReceiverProperties());
        validateRabbitProperties(services.getRabbitProperties());
    }

    private static void validateRabbitProperties(RabbitProperties properties){
        if (properties.getHost() == null) {
            throw new RuntimeException("Required environment property CAF_RABBITMQ_HOST must be set, currently has no value.");
        }
        Collection<String> consumeQueueNames = properties.getConsumeQueueNames();
        if(consumeQueueNames==null || consumeQueueNames.isEmpty()) {
            throw new RuntimeException("Required environment property CAF_RABBITMQ_CONSUME_QUEUES must be set, currently specifies no queues to watch.");
        }
    }

    private static void validateTaskReceiverProperties(TaskReceiverProperties properties){
        String outputDirStr = properties.getOutputDirectory();
        if(Strings.isNullOrEmpty(outputDirStr)){
            throw new RuntimeException("Required environment property "+ TaskReceiverProperties.OUTPUT_DIRECTORY_PROP_NAME
            + " must be set. Currently has no value.");
        }
        File outputDir = new File(outputDirStr);
        if(!outputDir.exists()){
            LOGGER.warn("The specified output directory for task messages does not exist. Attempting to create directory: "
                    +outputDirStr);
            boolean directoryCreateSuccess = outputDir.mkdir();

            if(!directoryCreateSuccess) {
                throw new RuntimeException("Error with environment property: "
                        + TaskReceiverProperties.OUTPUT_DIRECTORY_PROP_NAME
                        + ". Unable to create specified directory at specified path: " + outputDirStr);
            }
        }
        if(!outputDir.isDirectory()){
            throw new RuntimeException("Error with environment property: "
                    +TaskReceiverProperties.OUTPUT_DIRECTORY_PROP_NAME
                    + ". The specified output directory for task messages is not a directory: "+outputDirStr);
        }
    }
}
