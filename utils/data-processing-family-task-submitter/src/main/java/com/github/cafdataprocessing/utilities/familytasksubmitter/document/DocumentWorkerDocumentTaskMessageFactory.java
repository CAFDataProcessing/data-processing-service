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
package com.github.cafdataprocessing.utilities.familytasksubmitter.document;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;

import java.util.Collections;
import java.util.Map;

/**
 * Assists in creating Document Worker Document task messages.
 */
public final class DocumentWorkerDocumentTaskMessageFactory {
    private DocumentWorkerDocumentTaskMessageFactory(){}
    /**
     * Creates a DocumentWorkerTask for use on task data of a message.
     * @param document Document to set on task.
     * @param customData Custom data to set on task.
     * @return Constructed DocumentWorkerTask.
     */
    public static DocumentWorkerDocumentTask createTaskData(final DocumentWorkerDocument document,
                                                            final Map<String, String> customData){
        DocumentWorkerDocumentTask task = new DocumentWorkerDocumentTask();
        task.customData = customData;
        task.document = document;
        return task;
    }

    /**
     * Creates a task message with a DocumentWorkerDocumentTask set in the task data using provided parameters.
     * @param documentWorkerDocument Document to set in task data.
     * @param customData Custom data to set in task data.
     * @param outputQueueName Queue that task message should be sent to.
     * @param codec Codec to use in serializing task data for message.
     * @return Constructed TaskMessage.
     * @throws CodecException if exception occurs serializing task data.
     */
    public static TaskMessage createTaskMessage(final DocumentWorkerDocument documentWorkerDocument,
                                                final Map<String, String> customData,
                                                final String outputQueueName,
                                                final Codec codec)
            throws CodecException {
        byte[] taskdata = codec.serialise(DocumentWorkerDocumentTaskMessageFactory.createTaskData(documentWorkerDocument,
                customData));
        return new TaskMessage(documentWorkerDocument.reference, DocumentWorkerConstants.DOCUMENT_TASK_NAME,
                DocumentWorkerConstants.DOCUMENT_TASK_API_VER, taskdata,
                TaskStatus.NEW_TASK, Collections.emptyMap(), outputQueueName);
    }
}
