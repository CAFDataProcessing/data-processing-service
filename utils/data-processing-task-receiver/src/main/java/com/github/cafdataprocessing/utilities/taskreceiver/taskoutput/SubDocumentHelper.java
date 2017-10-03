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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Contains methods for assisting in the output of sub-documents.
 */
public class SubDocumentHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubDocumentHelper.class);

    /**
     * Determines the appropriate folder path to output data about a sub document to.
     * @param document Sub-document to determine folder path for.
     * @return Built folder path that should be used for the sub-document. Should be appended to an existing directory.
     */
    public static String determineSubDocumentFolder(SharedDocument document){
        //use the document reference as a base and extract out the levels, that lead to this sub document, to build up a filepath
        String subDocRef = document.getReference();
        //: is used in extract worker on created document refs for sub-documents and indicates levels and sub-document number
        //e.g. test.zip:1:1:1:0 would be the first file inside an archive nested multiple levels inside a zip
        String[] splitRefParts = subDocRef.split(":");
        if(splitRefParts.length < 2){
            LOGGER.warn("Encountered a sub-document message with unexpected form of document reference. Expected ':' to be present, splitting parent from sub-documents.");
            return null;
        }
        //first part will be the parent task ID
        StringBuilder outputFolderBuilder = new StringBuilder();
        outputFolderBuilder.append(FileNameHelper.sanitizeForFilesystem(splitRefParts[0]));

        //construct a path replicating the archive structure that leads to this sub-file. This should be consistent across sub-files in an archive
        //At any level it is possible to fully build the folder path without knowing details of the intermediate levels aside from their index given by the taskID.
        //e..g. test.zip/subDocs/1/subDocs/1/subDocs/1/subDocs/0
        for(int depthCounter = 1;depthCounter < splitRefParts.length; depthCounter++){
            String subDocumentIndex = splitRefParts[depthCounter];
            outputFolderBuilder
                .append(File.separator)
                .append("subDocs")
                .append(File.separator)
                .append(subDocumentIndex);
        }
        return outputFolderBuilder.toString();
    }

    /**
     * Determines if a provided document is a sub-document.
     * @param document Document to check.
     * @return True if this is a sub-document, otherwise false.
     */
    public static boolean isSubDocument(SharedDocument document){
        Optional<Map.Entry<String, String>> extractedFileMetadataEntry = document.getMetadata().stream()
                .filter(
                        md -> md.getKey().equalsIgnoreCase("KV_EXTRACTED_DOCUMENT_FILESIZE_BYTES"))
                .findFirst();
        if(extractedFileMetadataEntry.isPresent()){
            return true;
        }
        //missing a field that sub-documents are expected to have, as a final check if the field was removed during
        //processing check if the reference is structured like a sub-document i.e. using : to delineate sub-file position
        //e.g. test.zip:1:1:1:0
        return document.getReference().contains(":");
    }
}
