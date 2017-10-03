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
package com.github.cafdataprocessing.utilities.familytasksubmitter.taskmessage;

import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterConstants;
import com.github.cafdataprocessing.worker.policy.shared.Document;
import com.github.cafdataprocessing.worker.policy.shared.PolicyWorkerConstants;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.codec.JsonCodec;
import java.util.Collections;

/**
 * Represents a task message that may be sent to a queue.
 */
public class Message
{
    private static final Codec codec = new JsonCodec();
    private final Document document;
    private final String outputQueueName;

    public Message(final Document document, final String outputQueueName)
    {
        this.outputQueueName = outputQueueName;
        this.document = document;
    }

    public TaskMessage createTaskMessage() throws CodecException
    {
        byte[] taskdata = codec.serialise(PolicyTaskMessagesFactory.createTaskData(document,
                                                                                   getEnvironmentValue(
                                                                                       FamilyTaskSubmitterConstants.Message.PROJECT_ID,
                                                                                       "1"),
                                                                                   getEnvironmentValue(
                                                                                       FamilyTaskSubmitterConstants.OUTPUT_QUEUE_NAME,
                                                                                       "family-submitter-output-1"),
                                                                                   getEnvironmentValue(
                                                                                       FamilyTaskSubmitterConstants.Message.WORKFLOW_ID,
                                                                                       "1")));
        return new TaskMessage(document.getReference(), PolicyWorkerConstants.WORKER_NAME, PolicyWorkerConstants.API_VERSION, taskdata,
                               TaskStatus.NEW_TASK, Collections.emptyMap(), outputQueueName);
    }

    private static String getEnvironmentValue(final String name, final String defaultValue)
    {
        return System.getProperty(name, System.getenv(name) != null ? System.getenv(name) : defaultValue);
    }
}
