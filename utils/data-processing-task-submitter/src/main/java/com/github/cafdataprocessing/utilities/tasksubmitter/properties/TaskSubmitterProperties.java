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
package com.github.cafdataprocessing.utilities.tasksubmitter.properties;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Application specific properties such as input directory and workflow ID.
 */
@Configuration
@PropertySource("classpath:taskSubmitter.properties")
@PropertySource(value = "file:${CAF_CONFIG_LOCATION}/taskSubmitter.properties", ignoreResourceNotFound = true)
public class TaskSubmitterProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSubmitterProperties.class);

    @Autowired
    private Environment environment;
    public class PropertyNames {
        public class BaseData {
            public static final String BOILERPLATE_API_URL = "CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_API_URL";
            public static final String BOILERPLATE_BASE_DATA_INPUT_FILE = "CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_INPUT_FILE";
            public static final String BOILERPLATE_BASE_DATA_OUTPUT_FILE = "CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_OUTPUT_FILE";

            public static final String CLASSIFICATION_API_URL = "CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_API_URL";
            public static final String CLASSIFICATION_BASE_DATA_INPUT_FILE = "CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_INPUT_FILE";
            public static final String CLASSIFICATION_BASE_DATA_OUTPUT_FILE = "CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_OUTPUT_FILE";

            public static final String CREATE_BOILERPLATE_BASE_DATA = "CAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE";
            public static final String CREATE_CLASSIFICATION_BASE_DATA = "CAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION";

            public static final String EXTERNAL_API_READY_RETRY_ATTEMPTS = "CAF_TASKSUBMITTER_BASEDATA_EXTERNAL_API_READY_RETRY_ATTEMPTS";
            public static final String OVERWRITE_EXISTING = "CAF_TASKSUBMITTER_BASEDATA_OVERWRITE_EXISTING";

            public static final String PROCESSING_API_URL = "CAF_TASKSUBMITTER_BASEDATA_PROCESSING_API_URL";
            public static final String WORKFLOW_BASE_DATA_FILE = "CAF_TASKSUBMITTER_BASEDATA_WORKFLOW_INPUT_FILE";
        }

        public static final String DOCUMENT_INPUT_DIRECTORY = "CAF_TASKSUBMITTER_DOCUMENT_INPUT_DIRECTORY";
        public static final String PROJECT_ID = "CAF_TASKSUBMITTER_PROJECTID";
        public static final String SENT_DOCUMENTS_DIRECTORY = "CAF_TASKSUBMITTER_SENT_DOCUMENTS_DIRECTORY";
        public static final String WORKFLOW_ID = "CAF_TASKSUBMITTER_WORKFLOW_ID";
    }

    public String getBoilerplateApiUrl(){
        return environment.getProperty(PropertyNames.BaseData.BOILERPLATE_API_URL);
    }

    public String getBoilerplateBaseDataInputFile(){
        return environment.getProperty(PropertyNames.BaseData.BOILERPLATE_BASE_DATA_INPUT_FILE);
    }

    public String getBoilerplateBaseDataOutputFile(){
        return environment.getProperty(PropertyNames.BaseData.BOILERPLATE_BASE_DATA_OUTPUT_FILE);
    }

    public String getClassificationApiUrl(){
        return environment.getProperty(PropertyNames.BaseData.CLASSIFICATION_API_URL);
    }

    public String getClassificationBaseDataInputFile(){
        return environment.getProperty(PropertyNames.BaseData.CLASSIFICATION_BASE_DATA_INPUT_FILE);
    }

    public String getClassificationBaseDataOutputFile(){
        return environment.getProperty(PropertyNames.BaseData.CLASSIFICATION_BASE_DATA_OUTPUT_FILE);
    }

    public boolean getCreateBoilerplateBaseData(){
        return Boolean.parseBoolean(environment.getProperty(PropertyNames.BaseData.CREATE_BOILERPLATE_BASE_DATA));
    }

    public boolean getCreateClassificationBaseData(){
        return Boolean.parseBoolean(environment.getProperty(PropertyNames.BaseData.CREATE_CLASSIFICATION_BASE_DATA));
    }

    public String getDocumentInputDirectory() {
        return environment.getProperty(PropertyNames.DOCUMENT_INPUT_DIRECTORY);
    }

    public String getSentDocumentsDirectory(){
        return environment.getProperty(PropertyNames.SENT_DOCUMENTS_DIRECTORY);
    }

    public int getExternalApiRetryAttempts(){
        //defaulting to infinite retries
        int defaultValue = -1;
        String retryAttemptsStr = environment.getProperty(PropertyNames.BaseData.EXTERNAL_API_READY_RETRY_ATTEMPTS);
        if(Strings.isNullOrEmpty(retryAttemptsStr)){
            LOGGER.debug("No value provided for property "
                    +PropertyNames.BaseData.EXTERNAL_API_READY_RETRY_ATTEMPTS
                    +" default value will be used: "+defaultValue);
            return defaultValue;
        }
        try {
            return Integer.parseInt(retryAttemptsStr);
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("Unable to convert property "
                    +PropertyNames.BaseData.EXTERNAL_API_READY_RETRY_ATTEMPTS+
                    " to a valid number.");
        }
    }

    public boolean getOverwriteExistingBaseData(){
        String overwriteStr =  environment.getProperty(PropertyNames.BaseData.OVERWRITE_EXISTING);
        if(Strings.isNullOrEmpty(overwriteStr)){
            return true;
        }
        return Boolean.parseBoolean(overwriteStr);
    }

    public String getProcessingApiUrl(){
        return environment.getProperty(PropertyNames.BaseData.PROCESSING_API_URL);
    }

    public String getProjectId(){
        return environment.getProperty(PropertyNames.PROJECT_ID);
    }

    public String getWorkflowBaseDataFile(){
        return environment.getProperty(PropertyNames.BaseData.WORKFLOW_BASE_DATA_FILE);
    }

    public Long getWorkflowId() {
        String workflowIdStr = environment.getProperty(PropertyNames.WORKFLOW_ID);
        if(Strings.isNullOrEmpty(workflowIdStr)){
            return null;
        }
        try {
            return Long.valueOf(workflowIdStr);
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("Unable to convert property "+PropertyNames.WORKFLOW_ID+" to a valid number.");
        }
    }
}
