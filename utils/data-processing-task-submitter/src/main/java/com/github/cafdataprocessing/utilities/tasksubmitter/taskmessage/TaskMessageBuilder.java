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
import com.github.cafdataprocessing.worker.policy.shared.Document;
import com.github.cafdataprocessing.worker.policy.shared.TaskData;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.api.worker.TaskStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility Class that creates a Policy Worker TaskMessage for each document in the specified directory.
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
    public static List<FileAndTaskMessage> buildTaskMessagesForDirectory(Long workflowId, String projectId,
                                                                  String directory)
            throws ConfigurationException, CodecException, IOException, DataStoreException, InterruptedException {
        if (workflowId == null) {
            throw new ConfigurationException("No workflow Id specified");
        }

        // Get list of Document objects which represent each of the files we read from disk
        List<DocumentAndFile> documentsToSend = PolicyDocumentsBuilder.getDocuments(directory);

        //Construct a task to send to the policy worker for each document.
        List<FileAndTaskMessage> messagesToSend = new ArrayList<>();
        for (DocumentAndFile documentAndFile : documentsToSend) {
            TaskMessage taskMessage = buildTaskMessage(documentAndFile.getDocument(), workflowId, projectId, dataDir);
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
    public static FileAndTaskMessage buildTaskMessageForDocument(Long workflowId, String projectId, String documentPath)
            throws ConfigurationException, CodecException, IOException, DataStoreException {
        if (workflowId == null) {
            throw new ConfigurationException("No workflow Id specified");
        }
        //Build Policy document from path to document
        DocumentAndFile documentAndFile = PolicyDocumentsBuilder.getDocument(documentPath);
        TaskMessage taskMessage = buildTaskMessage(documentAndFile.getDocument(), workflowId, projectId, dataDir);
        return new FileAndTaskMessage(documentAndFile.getFile(), taskMessage);
    }

    private static TaskMessage buildTaskMessage(Document document, long workflowId, String projectId, String storageDirectory)
            throws CodecException {
        TaskData taskData = new TaskData();
        taskData.setDocument(document);
        taskData.setOutputPartialReference(storageDirectory);
        taskData.setWorkflowId(String.valueOf(workflowId));
        taskData.setExecutePolicyOnClassifiedDocuments(true);
        taskData.setProjectId(projectId);
        byte[] serializedTaskData = codec.serialise(taskData);
        TaskMessage taskMessage = new TaskMessage();
        taskMessage.setTaskData(serializedTaskData);
        taskMessage.setContext(new HashMap<>());
        taskMessage.setTaskId(String.valueOf(messageCount.addAndGet(1)) + "-" + document.getReference());
        taskMessage.setTaskClassifier("PolicyWorker");
        taskMessage.setTaskApiVersion(1);
        taskMessage.setTaskStatus(TaskStatus.NEW_TASK);
        taskMessage.setTo(destinationQueue);
        return taskMessage;
    }
}
