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
package com.github.cafdataprocessing.utilities.familytasksubmitter;

import com.google.common.base.Strings;

/**
 * Provides access to configuration properties for the family task submitter.
 */
public class FamilyTaskSubmitterProperties {

    private static String getEnvironmentValue(final String name, final String defaultValue)
    {
        String envResult = System.getenv(name);
        return System.getProperty(name, envResult != null ? envResult : defaultValue);
    }

    public static String getContentFieldName() {
        return getEnvironmentValue(FamilyTaskSubmitterConstants.CONTENT_FIELD_NAME,
                FamilyTaskSubmitterConstants.DEFAULT_CONTENT_FIELD_NAME);
    }

    public static String getFamilyReference() {
        return getEnvironmentValue(FamilyTaskSubmitterConstants.FAMILY_REFERENCE, "");
    }

    public static String getOutputPartialReference() {
        return getEnvironmentValue(FamilyTaskSubmitterConstants.Message.OUTPUT_PARTIAL_REFERENCE, "");
    }

    public static String getOutputQueueName() {
        return getEnvironmentValue(FamilyTaskSubmitterConstants.OUTPUT_QUEUE_NAME, "");
    }

    public static String getProjectId() {
        return getEnvironmentValue(FamilyTaskSubmitterConstants.Message.PROJECT_ID, "1");
    }

    public static String getRabbitHost() {
        String rabbitHost = getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.CAF_RABBITMQ_HOST, "");
        if(!Strings.isNullOrEmpty(rabbitHost)){
            return rabbitHost;
        }
        return getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.RABBIT_HOST, "localhost");
    }

    public static String getRabbitPassword() {
        String rabbitPassword = getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.CAF_RABBITMQ_PASSWORD, "");
        if(!Strings.isNullOrEmpty(rabbitPassword)) {
            return rabbitPassword;
        }
        return getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.RABBIT_PASSWORD, "guest");
    }

    public static Integer getRabbitPort() {
        String rabbitPortStr = getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.CAF_RABBITMQ_PORT, "");
        if(!Strings.isNullOrEmpty(rabbitPortStr)) {
            return Integer.parseInt(rabbitPortStr);
        }
        return Integer.parseInt(getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.RABBIT_PORT, "9549"));
    }

    public static String getRabbitUsername() {
        String rabbitUsername = getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.CAF_RABBITMQ_USERNAME, "");
        if(!Strings.isNullOrEmpty(rabbitUsername)) {
            return rabbitUsername;
        }
        return getEnvironmentValue(FamilyTaskSubmitterConstants.RabbitConstants.RABBIT_USER, "guest");
    }

    public static Long getWorkflowId() {
        return Long.parseLong(getEnvironmentValue(FamilyTaskSubmitterConstants.Message.WORKFLOW_ID, "1"));
    }
}
