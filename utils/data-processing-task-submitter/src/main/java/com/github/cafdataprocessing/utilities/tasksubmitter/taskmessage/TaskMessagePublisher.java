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
package com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage;

import com.github.cafdataprocessing.utilities.tasksubmitter.DocumentsMover;
import com.github.cafdataprocessing.utilities.tasksubmitter.monitor.DirectoryWatcher;
import com.github.cafdataprocessing.utilities.tasksubmitter.services.QueueServices;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.worker.DataStoreException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Responsible for publishing task messages.
 */
public class TaskMessagePublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMessagePublisher.class);

    /**
     * Constructs and publishes policy worker task messages for files in provided directory. Directory will be monitored
     * and any further file additions will have task messages published also.
     * @param documentInputDirectory Directory to publish messages for.
     * @param workflowId Workflow ID to set on the task data of messages.
     * @param projectId Project ID to set on the task data of messages.
     * @param sentDocumentsDirectory Directory to move files that have had messages sent. Pass null to not move files.
     * @return ExecutorService monitoring the director for new files.
     * @throws IOException If errors occurs setting up monitoring for specified directory.
     */
    public static ExecutorService publishTaskMessagesForDirectoryAndMonitor(String documentInputDirectory,
                                                                            Long workflowId,
                                                                            String projectId,
                                                                            String sentDocumentsDirectory)
            throws IOException {
        TaskMessagePublisher.publishTaskMessagesForDirectory(documentInputDirectory, workflowId, projectId,
                sentDocumentsDirectory);

        //monitor directory for new files being added
        return setupMonitorInputDirectory(workflowId, projectId, documentInputDirectory,
                sentDocumentsDirectory);
    }

    /**
     * Constructs and publishes policy worker task messages for files in provided directory.
     * @param documentInputDirectory Directory to publish messages for.
     * @param workflowId Workflow ID to set on the task data of messages.
     * @param projectId Project ID to set on the task data of messages.
     * @param sentDocumentsDirectory Directory to move files that have had messages sent. Pass null to not move files.
     */
    public static void publishTaskMessagesForDirectory(String documentInputDirectory, Long workflowId, String projectId,
                                                 String sentDocumentsDirectory){
        //construct task message for each document in the folder
        LOGGER.info("Building task messages for input directory: "+documentInputDirectory);
        List<FileAndTaskMessage> taskMessages;
        try {
            taskMessages = TaskMessageBuilder.buildTaskMessagesForDirectory(workflowId, projectId,
                    documentInputDirectory);
        } catch (ConfigurationException | CodecException | IOException |
                DataStoreException | InterruptedException e) {
            LOGGER.error("Failure occurred trying to build task messages for input directory.", e);
            return;
        }

        if(taskMessages.isEmpty()){
            LOGGER.info("No task messages were built for the directory.");
            return;
        }

        //publish all task messages
        LOGGER.info("Preparing to submit task messages with workflow ID: "+ workflowId + " and projectId: "+projectId);
        try {
            QueueServices.publishAllMessages(taskMessages);
        } catch (IOException | CodecException e) {
            throw new RuntimeException("Failure occurred trying to publish task messages for input directory.", e);
        }

        DocumentsMover.moveDocumentsFromTaskMessages(taskMessages, sentDocumentsDirectory, documentInputDirectory);
    }

    private static ExecutorService setupMonitorInputDirectory(long workflowId, String projectId, String inputDirectory,
                                                              String sentDocumentsDirectory) throws IOException {
        return DirectoryWatcher.watchDirectoryForNewFiles(inputDirectory,
                fileObject -> createAndSendTasksForNewFiles(fileObject, workflowId, projectId, sentDocumentsDirectory,
                        inputDirectory)
        );
    }

    private static void createAndSendTasksForNewFiles(FileObject fileObject, long workflowId, String projectId,
                                                      String sentDocumentsDirectory, String inputDirectory){
        boolean isFolder;
        try{
            isFolder = fileObject.isFolder();
        }
        catch(FileSystemException e){
            throw new RuntimeException("Error trying to build task messages, could not determine if new entry was a folder or file: "
                    +fileObject.getName().getURI(), e);
        }

        FileAndTaskMessage taskMessage;
        if(isFolder){
            //don't create entries for folder as the individual documents under the folder will be passed to this method
            return;
        }
        else {
            try {
                taskMessage = TaskMessageBuilder.buildTaskMessageForDocument(workflowId,
                        projectId, fileObject.getName().getURI());
            } catch (ConfigurationException | CodecException | DataStoreException | IOException e) {
                LOGGER.error("Error trying to build task messages for path: "
                        + fileObject.getName().getURI(), e);
                return;
            }
        }
        //Publish task messages for all documents in the directory to Workflow Worker's input queue.
        try {
            QueueServices.publishMessage(taskMessage);
        } catch (IOException | CodecException e){
            throw new RuntimeException("Error trying to publish task message for path: "
                    +fileObject.getName().getURI(), e);
        }
        DocumentsMover.moveDocumentFromTaskMessage(taskMessage, sentDocumentsDirectory, inputDirectory);
    }
}
