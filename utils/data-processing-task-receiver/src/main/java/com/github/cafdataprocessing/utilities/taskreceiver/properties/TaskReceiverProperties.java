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
package com.github.cafdataprocessing.utilities.taskreceiver.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Properties specific to this application.
 */
@Configuration
@PropertySource("classpath:taskReceiver.properties")
@PropertySource(value = "file:${CAF_CONFIG_LOCATION}/taskReceiver.properties", ignoreResourceNotFound = true)
public class TaskReceiverProperties {
    @Autowired
    private Environment environment;

    public static final String OUTPUT_DIRECTORY_PROP_NAME = "CAF_TASKRECEIVER_OUTPUT_DIRECTORY";

    public static final String SAVE_TASK_DATA_ONLY = "CAF_TASKRECEIVER_SAVE_TASK_DATA_ONLY";

    public static final String CLEANUP_DATASTORE_AFTER_PROCESSING = "CAF_TASKRECEIVER_CLEANUP_DATASTORE_AFTER_PROCESSING";


    public String getOutputDirectory(){
        return environment.getProperty(OUTPUT_DIRECTORY_PROP_NAME);
    }

    public String getIsSaveTaskDataOnly() { return environment.getProperty(SAVE_TASK_DATA_ONLY); }

    public String getCleanupDataStoreAfterProcessing() { return environment.getProperty(CLEANUP_DATASTORE_AFTER_PROCESSING); }
}
