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
package com.github.cafdataprocessing.utilities.familytasksubmitter;

/**
 *
 * @author mcgreeva
 */
public final class FamilyTaskSubmitterConstants
{
    private FamilyTaskSubmitterConstants()
    {

    }

    public static final String IS_FAMILY_ORIGIN = "IS_FAMILY_ORIGIN";
    public static final String IS_ROOT = "IS_ROOT";
    public static final String PARENT_REFERENCE = "PARENT_reference";
    public static final String CHILD_INFO_COUNT = "CHILD_INFO_COUNT";
    public static final String OUTPUT_QUEUE_NAME = "OUTPUT_QUEUE_NAME";
    public static final String ROOT_REFERENCE = "ROOT_REFERENCE";

    public final class RabbitConstants
    {
        public static final String RABBIT_HOST = "RABBIT_HOST";
        public static final String RABBIT_PORT = "RABBIT_PORT";
        public static final String RABBIT_USER = "RABBIT_USER";
        public static final String RABBIT_PASSWORD = "RABBIT_PASSWORD";

    }

    public final class Message
    {
        public static final String WORKFLOW_ID = "WORKFLOW_ID";
        public static final String PROJECT_ID = "PROJECT_ID";
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
