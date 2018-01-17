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
package com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage;

import com.github.cafdataprocessing.utilities.tasksubmitter.services.Services;
import com.github.cafdataprocessing.workflow.constants.WorkflowWorkerConstants;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.api.worker.TaskStatus;
import com.hpe.caf.worker.document.DocumentWorkerConstants;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility Class that creates a Document Worker Document TaskMessage for each document in the specified directory.
 */
public class TaskMessageBuilder {
    private final static Codec codec = Services.getInstance().getCodec();
    private final static AtomicInteger messageCount = new AtomicInteger();
    private final static String dataDir = Services.getInstance().getStorageConfiguration().getDataDir();
    private final static String destinationQueue = Services.getInstance().getRabbitProperties().getPublishQueue();

    /**
     * Constructs a task message for each file in the defined directory.
     *
     * @param workflowId  The Workflow ID to set on the built task.
     * @param projectId   The ProjectId to set on the built task. Workflow ID provided should be under this project ID.
     * @param directory   The directory to build a task message per document for.
     * @return The constructed task messages and their associated files.
     * @throws ConfigurationException If invalid configuration passed.
     * @throws CodecException If a codec error occurs building a task message.
     * @throws IOException If error occurs accessing files in directory.
     * @throws DataStoreException If error occurs storing data for files.
     */
    public static List<FileAndTaskMessage> buildTaskMessagesForDirectory(final Long workflowId, final String projectId,
                                                                         final String directory)
            throws ConfigurationException, CodecException, IOException, DataStoreException, InterruptedException {
        if (workflowId == null) {
            throw new ConfigurationException("No workflow Id specified");
        }

        // Get list of Document objects which represent each of the files we read from disk
        final List<DocumentAndFile> documentsToSend = DocumentWorkerDocumentsBuilder.getDocuments(directory);

        //Construct a task to send to the worker for each document.
        final List<FileAndTaskMessage> messagesToSend = new ArrayList<>();
        for (DocumentAndFile documentAndFile : documentsToSend) {
            final TaskMessage taskMessage = buildTaskMessage(documentAndFile.getDocument(), workflowId, projectId, dataDir);
            messagesToSend.add(new FileAndTaskMessage(documentAndFile.getFile(), taskMessage));
        }
        return messagesToSend;
    }

    /**
     * Constructs a task message for the document at specified path.
     * @param workflowId  The Workflow ID to set on the built task.
     * @param projectId   The ProjectId to set on the built task. Workflow ID provided should be under this project ID.
     * @param documentPath Path to the document to construct task for.
     * @return Constructed task message and the associated file.
     * @throws ConfigurationException If invalid configuration passed.
     * @throws CodecException If a codec error occurs building a task message.
     * @throws IOException If error occurs accessing the file.
     * @throws DataStoreException If error occurs storing data for file.
     */
    public static FileAndTaskMessage buildTaskMessageForDocument(final Long workflowId, final String projectId,
                                                                 final String documentPath)
            throws ConfigurationException, CodecException, IOException, DataStoreException {
        if (workflowId == null) {
            throw new ConfigurationException("No workflow Id specified");
        }
        //Build document from path to document
        final DocumentAndFile documentAndFile = DocumentWorkerDocumentsBuilder.getDocument(documentPath);
        final TaskMessage taskMessage = buildTaskMessage(documentAndFile.getDocument(), workflowId, projectId, dataDir);
        return new FileAndTaskMessage(documentAndFile.getFile(), taskMessage);
    }

    private static TaskMessage buildTaskMessage(final DocumentWorkerDocument document, final long workflowId,
                                                final String projectId, final String storageDirectory)
            throws CodecException {
        final DocumentWorkerDocumentTask taskData = new DocumentWorkerDocumentTask();
        final Map<String, String> customData = new HashMap<>();
        customData.put(WorkflowWorkerConstants.CustomData.OUTPUT_PARTIAL_REFERENCE, storageDirectory);
        customData.put(WorkflowWorkerConstants.CustomData.PROJECT_ID, projectId);
        customData.put(WorkflowWorkerConstants.CustomData.WORKFLOW_ID, Long.toString(workflowId));
        taskData.customData = customData;
        taskData.document = document;
        final byte[] serializedTaskData = codec.serialise(taskData);
        final TaskMessage taskMessage = new TaskMessage(String.valueOf(messageCount.addAndGet(1)) + "-" + document.reference,
                DocumentWorkerConstants.DOCUMENT_TASK_NAME,
                DocumentWorkerConstants.DOCUMENT_TASK_API_VER, serializedTaskData,
                TaskStatus.NEW_TASK, Collections.emptyMap(), destinationQueue);
        return taskMessage;
    }
}
