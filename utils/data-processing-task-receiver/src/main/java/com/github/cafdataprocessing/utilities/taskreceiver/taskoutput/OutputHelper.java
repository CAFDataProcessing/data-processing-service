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
package com.github.cafdataprocessing.utilities.taskreceiver.taskoutput;

import com.github.cafdataprocessing.worker.policy.handlers.shared.document.SharedDocument;
import com.hpe.caf.api.worker.TaskMessage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Provides methods to output data to the storage medium.
 */
public class OutputHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputHelper.class);

    /**
     * Outputs provided string content to a specified file in the destination provided.
     * @param outputDestination Folder to write output file to.
     * @param filename Filename to give output file.
     * @param fileContent Content for output file.
     */
    public void outputString(String outputDestination, String filename, String fileContent) throws IOException {
        File taskMessageFile = new File(outputDestination, filename);
        if(taskMessageFile.exists()){
            LOGGER.warn("File already exists in folder: "+outputDestination, ", filename: "+filename);
        }
        try {
            FileUtils.writeStringToFile(taskMessageFile, fileContent);
        } catch (IOException e) {
            throw new IOException("Failure outputting string to file: "
                    +taskMessageFile.getAbsolutePath(),
                    e);
        }
    }

    /**
     * Outputs provided input stream to a specified file in the destination provided.
     * @param messageOutputDir Folder to write output file to.
     * @param filename Filename to give output file.
     * @param streamToOutput Stream containing content to write to the output file.
     */
    public void outputStream(String messageOutputDir, String filename, InputStream streamToOutput) throws IOException {
        File documentFile = new File(messageOutputDir, filename);
        //figure out an extension to use
        try {
            Files.copy(streamToOutput, documentFile.toPath());
        } catch (IOException e) {
            throw new IOException("Failure outputting stream to file: "+documentFile.getAbsolutePath(),
                    e);
        }
    }

    /**
     * Creates a folder in the specified directory, using the ID of the provided task message.
     * @param message Task Message to pull ID from.
     * @param outputDirectory Directory to create new folder.
     * @return Path to the new folder.
     */
    public String createFolderFromTaskMessageId(TaskMessage message, String outputDirectory){
        String taskId = message.getTaskId();
        File taskFolder = new File(outputDirectory, FileNameHelper.sanitizeForFilesystem(taskId));
        if(!taskFolder.exists() || !taskFolder.isDirectory()){
            taskFolder.mkdir();
        }
        return taskFolder.getAbsolutePath();
    }

    /**
     * Makes and returns the appropriate folder to output the provided document to.
     * @param document Document to determine folder for.
     * @param outputDirectory Directory under which the document folder should be placed.
     * @return Path to the folder document data should be output to.
     */
    public String createFolderFromDocument(SharedDocument document,
                                                       String outputDirectory) {
        if(SubDocumentHelper.isSubDocument(document)){
            //determine appropriate folder to create the sub-document files under
            String outputDirectoryForMessage = SubDocumentHelper.determineSubDocumentFolder(document);
            if(outputDirectoryForMessage==null){
                LOGGER.warn("Unable to determine a parent to add sub-document task message output under, falling back to creating folder matching task ID on message.");
                return createTaskFolderBaseOnDocRef(document, outputDirectory).getAbsolutePath();
            }
            File taskFolder = new File(outputDirectory, outputDirectoryForMessage);
            if(!taskFolder.exists() || !taskFolder.isDirectory()){
                taskFolder.mkdir();
            }
            return taskFolder.getAbsolutePath();
        }
        else{
            return createTaskFolderBaseOnDocRef(document, outputDirectory).getAbsolutePath();
        }
    }

    private File createTaskFolderBaseOnDocRef(SharedDocument document, String outputDirectory){
        String documentReference = document.getReference();
        String sanitizedTaskId = FileNameHelper.sanitizeForFilesystem(documentReference);
        //create folder to store files if it doesn't exist already
        File taskFolder = new File(outputDirectory, sanitizedTaskId);
        if(!taskFolder.exists() || !taskFolder.isDirectory()){
            taskFolder.mkdir();
        }
        return taskFolder;
    }
}
