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

import com.github.cafdataprocessing.worker.policy.handlers.shared.document.SharedDocument;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.api.worker.TaskSourceInfo;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.api.worker.TrackingInfo;
import com.github.cafdataprocessing.utilities.taskreceiver.services.Services;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience class to build Task Messages for test purposes.
 */
public class TestTaskMessageBuilder {
    private static final int messageVersion = 3;
    private static final String messageClassifier = "Worker";
    private static final int messageTaskApiVersion = 1;
    private static int messageTaskIdCounter = 1;
    private static final String messageTo = "testTo";
    private static final String jobTaskId = "job_test";
    private static final String messageSourceName = "Worker";
    private static final String messageSourceVersion = "1.19.0";
    private static final Codec codec = Services.getInstance().getCodec();
    private static final String messageContextProjectId = "DefaultProjectId";
    private static final long messageContextWorkflowId = 1;

    public static void incrementMessageCounter(){
        messageTaskIdCounter++;
    }

    public static TaskMessage buildTaskMessage(SharedDocument document) throws CodecException {
        return buildTaskMessage(document, null);
    }

    public static TaskMessage buildTaskMessage(byte[] taskData) throws CodecException {
        return buildTaskMessage(taskData, null);
    }

    public static TaskMessage buildTaskMessage(byte[] taskData, String taskId) throws CodecException {
        TaskMessage message = buildTaskMessageWithNoTaskId(taskData);
        if(taskId==null) {
            message.setTaskId(Integer.toString(messageTaskIdCounter));
        }
        else{
            message.setTaskId(taskId);
        }
        return message;
    }

    public static TaskMessage buildTaskMessage(SharedDocument document, String taskId) throws CodecException {
        TaskMessage message = buildTaskMessageWithNoTaskId(codec.serialise(document));
        if(taskId==null) {
            message.setTaskId(Integer.toString(messageTaskIdCounter));
        }
        else{
            message.setTaskId(taskId);
        }
        return message;
    }

    public static TaskMessage buildTaskMessageWithNoTaskId(byte[] taskData) throws CodecException {
        TaskMessage message = new TaskMessage();
        message.setVersion(messageVersion);
        message.setTaskClassifier(messageClassifier);
        message.setTaskApiVersion(messageTaskApiVersion);
        message.setTaskStatus(TaskStatus.NEW_TASK);
        message.setTo(messageTo);
        TrackingInfo info = new TrackingInfo();
        info.setJobTaskId(jobTaskId);
        message.setTracking(info);
        TaskSourceInfo sourceInfo = new TaskSourceInfo();
        sourceInfo.setName(messageSourceName);
        sourceInfo.setVersion(messageSourceVersion);
        message.setSourceInfo(sourceInfo);

        //task data
        message.setTaskData(taskData);

        //context
        Map<String, byte[]> contextMap = new HashMap<>();
        TestContext testContext = new TestContext();
        testContext.setProjectId(messageContextProjectId);
        testContext.setWorkflowId(messageContextWorkflowId);

        contextMap.put("caf/dataprocessing-fs/workflow", codec.serialise(testContext));
        message.setContext(contextMap);
        return message;
    }
}
