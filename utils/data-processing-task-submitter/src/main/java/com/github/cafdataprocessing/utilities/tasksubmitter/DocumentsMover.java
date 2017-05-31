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
package com.github.cafdataprocessing.utilities.tasksubmitter;

import com.google.common.base.Strings;
import com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage.FileAndTaskMessage;
import org.apache.commons.vfs2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Responsible for moving documents that have had task messages submitted.
 */
public class DocumentsMover {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentsMover.class);
    private static FileSystemManager fsManager;
    private static MatchFileSelector fileSelector = new MatchFileSelector();

    static {
        try {
            fsManager = VFS.getManager();
        } catch (FileSystemException e) {
            LOGGER.warn("Error while setting up file manager to move submitted files. Files will not be moved.", e);
        }
    }

    /**
     * Moves the provided files to the destination folder.
     * @param filesAndMessages Files that should be moved.
     * @param destinationFolder Folder to move files to.
     * @param rootSourceDirectory Original root folder than was monitored. Used to maintain folder structure underneath
     *                            this location when moving files.
     */
    public static void moveDocumentsFromTaskMessages(List<FileAndTaskMessage> filesAndMessages,
                                                     String destinationFolder,
                                                     String rootSourceDirectory){
        if(Strings.isNullOrEmpty(destinationFolder)){
            LOGGER.debug("Folder to move documents to that have been sent for processing is not set. Documents will not be moved.");
            return;
        }
        for(FileAndTaskMessage fileAndMessage: filesAndMessages){
            moveDocumentFromTaskMessage(fileAndMessage, destinationFolder, rootSourceDirectory);
        }
    }

    public static void moveDocumentFromTaskMessage(FileAndTaskMessage fileAndMessage, String destinationFolder,
                                                   String rootSourceDirectory){
        if(fsManager==null){
            LOGGER.debug("File manager is not initialized, likely due to error on startup. File will not be moved.");
            return;
        }

        if(Strings.isNullOrEmpty(destinationFolder)){
            LOGGER.debug("Folder to move file has have been sent for processing is not set. File will not be moved.");
            return;
        }

        FileObject fileToMove = fileAndMessage.getFile();
        String destinationFilePath;
        try {
            destinationFilePath = buildFilepathForFileToMove(fileToMove, destinationFolder, rootSourceDirectory);
        } catch (FileSystemException e) {
            LOGGER.warn("Error while attempting to build file name when moving file that was sent for processing. File will not be moved. ",
                    e);
            return;
        }
        FileObject moveLocation;
        try {
            moveLocation = fsManager.resolveFile(destinationFilePath);
        } catch (FileSystemException e) {
            LOGGER.warn("Error while attempting to prepare location that file that was sent for processing was to be moved to. File will not be moved.",
                    e);
            return;
        }
        try {
            FileObject moveToFolder = moveLocation.getParent();
            if(!moveToFolder.exists()) {
                moveLocation.createFolder();
            }
        } catch (FileSystemException e) {
            LOGGER.warn("Error while attempting to create folders to store file that was sent for processing. File will not be moved.",
                    e);
            return;
        }
        try {
            // using copy and then delete here rather than moveTo as moveTo has been observed to fail on linux box at the
            // Java File rename method call with no discernible cause (method only returns false indicating a failure occurred)
            moveLocation.copyFrom(fileToMove, fileSelector);
            fileToMove.delete();
        } catch (FileSystemException e) {
            LOGGER.warn("Error while attempting to move file that was sent for processing. File will not be moved.", e);
            return;
        }
        LOGGER.info("Moved file : "+fileToMove.getName().getFriendlyURI() + ", to location: "+destinationFilePath);
    }

    private static String buildFilepathForFileToMove(FileObject fileToMove, String destinationFolder,
                                                     String rootSourceDirectoryStr) throws FileSystemException {
        String fileToMoveUri = fileToMove.getName().getFriendlyURI();

        //preserve the folder that file was inside the input folder by removing the root input directory from the URI
        FileObject rootSourceDir = fsManager.resolveFile(rootSourceDirectoryStr);
        String rootSourceDirUri = rootSourceDir.getName().getFriendlyURI();
        String fileToMoveNameWithFolder = fileToMoveUri.substring(rootSourceDirUri.length());

        String destinationFilePath;
        if(destinationFolder.endsWith("/")){
            destinationFilePath = destinationFolder + fileToMoveNameWithFolder;
        }
        else{
            destinationFilePath = destinationFolder + '/' + fileToMoveNameWithFolder;
        }
        return destinationFilePath;
    }

    static class MatchFileSelector implements FileSelector{
        @Override
        public boolean includeFile(FileSelectInfo fileSelectInfo) throws Exception {
            return true;
        }
        @Override
        public boolean traverseDescendents(FileSelectInfo fileSelectInfo) throws Exception {
            return false;
        }
    }
}
