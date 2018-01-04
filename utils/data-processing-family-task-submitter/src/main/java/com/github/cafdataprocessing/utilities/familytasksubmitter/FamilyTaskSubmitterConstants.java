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

/**
 *
 * @author mcgreeva
 */
public final class FamilyTaskSubmitterConstants
{
    private FamilyTaskSubmitterConstants() {}

    public static final String CHILD_INFO_COUNT = "CHILD_INFO_COUNT";
    public static final String OUTPUT_QUEUE_NAME = "OUTPUT_QUEUE_NAME";
    public static final String FAMILY_REFERENCE = "FAMILY_REFERENCE";
    public static final String CONTENT_FIELD_NAME = "CONTENT_FIELD_NAME";
    public static final String DEFAULT_CONTENT_FIELD_NAME = "CONTENT_PRIMARY";

    public final class RabbitConstants
    {
        public static final String CAF_RABBITMQ_HOST = "CAF_RABBITMQ_HOST";
        public static final String CAF_RABBITMQ_PASSWORD = "CAF_RABBITMQ_PASSWORD";
        public static final String CAF_RABBITMQ_PORT = "CAF_RABBITMQ_PORT";
        public static final String CAF_RABBITMQ_USERNAME = "CAF_RABBITMQ_USERNAME";
        @Deprecated
        public static final String RABBIT_HOST = "RABBIT_HOST";
        @Deprecated
        public static final String RABBIT_PORT = "RABBIT_PORT";
        @Deprecated
        public static final String RABBIT_USER = "RABBIT_USER";
        @Deprecated
        public static final String RABBIT_PASSWORD = "RABBIT_PASSWORD";

    }

    public final class Message
    {
        public static final String OUTPUT_PARTIAL_REFERENCE = "OUTPUT_PARTIAL_REFERENCE";
        public static final String PROJECT_ID = "PROJECT_ID";
        public static final String WORKFLOW_ID = "WORKFLOW_ID";
    }
    
    public final class Elastic
    {
        public static final String CAF_ELASTIC_CLUSTERNAME = "CAF_ELASTIC_CLUSTERNAME";
        public static final String CAF_ELASTIC_INDEX_NAME = "CAF_ELASTIC_INDEX_NAME";
        public static final String CAF_ELASTIC_TYPE = "CAF_ELASTIC_TYPE";
        public static final String CAF_ELASTIC_HOSTNAME = "CAF_ELASTIC_HOSTNAME";
        public static final String CAF_ELASTIC_TRANSPORT_PORT = "CAF_ELASTIC_TRANSPORT_PORT";
    }
}
