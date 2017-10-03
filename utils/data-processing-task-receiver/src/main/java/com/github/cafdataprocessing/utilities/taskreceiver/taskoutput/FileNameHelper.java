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
import com.google.common.base.Strings;
import com.hpe.caf.util.ref.ReferencedData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Provides facilities to determine file names to use for documents.
 */
public class FileNameHelper {

    private static final List<Character> illegalFilesystemChars = Arrays.asList('\\', '/', ':', '*', '?', '\'', '"', '<', '>', '|');

    /**
     * Replaces illegal filesystem characters in provided string with '_'.
     * @param strToSanitize String that should be sanitized.
     * @return Version of the string parameter with any illegal filesystem characters replaced with '_'.
     */
    public static String sanitizeForFilesystem(String strToSanitize){
        StringBuilder strBuilder = new StringBuilder();
        for(int charIndex = 0; charIndex < strToSanitize.length(); charIndex++){
            char currentChar = strToSanitize.charAt(charIndex);
            if(illegalFilesystemChars.contains(currentChar)){
                strBuilder.append("_");
            }
            else{
                strBuilder.append(currentChar);
            }
        }
        return strBuilder.toString();
    }

    /**
     * Gets a file name to use for the provided metadata reference
     * @param metadataReference Metadata Reference to generate output filename for.
     * @return Constructed metadata reference filename.
     */
    public static String getMetadataRefOutputFilename(Map.Entry<String, ReferencedData> metadataReference){
        String fieldName = metadataReference.getKey();
        String storageReference = metadataReference.getValue().getReference();
        if(Strings.isNullOrEmpty(storageReference)){
            return "field_"+sanitizeForFilesystem(fieldName);
        }
        return "field_"+sanitizeForFilesystem(fieldName) +"." +sanitizeForFilesystem(storageReference);
    }

    /**
     * Gets a name to use for the output file representing the original document passed in.
     * @param document Document to generate filename for.
     * @return Generated filename.
     */
    public static String getDocumentOutputFilename(SharedDocument document){
        String outputPrefix = "file_";
        String storageReference = null;
        String fileName = null;
        String title = null;
        String importMagicExtension = null;
        String reference =  null;
        //use FILENAME field if available on document
        for (final Map.Entry<String, String> entry : document.getMetadata()) {
            String entryKey = entry.getKey();
            if(entryKey.equalsIgnoreCase("IMPORTMAGICEXTENSION")){
                importMagicExtension = entry.getValue();
                continue;
            }
            if(entryKey.equalsIgnoreCase("FILENAME")){
                fileName = entry.getValue();
                continue;
            }
            if(entryKey.equalsIgnoreCase("TITLE")){
                title = entry.getValue();
                continue;
            }
            if(entryKey.equalsIgnoreCase("storageReference")){
                storageReference = entry.getValue();
                continue;
            }
            if(entryKey.equalsIgnoreCase("reference")){
                reference = entry.getValue();
                continue;
            }
        }
        String outputFilenameSuffix = null;
        //expecting title and filename to have extension on the end so returning those as is.
        if(!Strings.isNullOrEmpty(fileName)){
            if(fileName.indexOf(".")==-1 && !Strings.isNullOrEmpty(importMagicExtension)){
                //add extension to end of this if available
                fileName = fileName + "." + importMagicExtension;
            }
            outputFilenameSuffix = sanitizeForFilesystem(fileName);
        }
        else if(!Strings.isNullOrEmpty(title)){
            if(title.indexOf(".")==-1 && !Strings.isNullOrEmpty(importMagicExtension)){
                //add extension to end of this if available
                title = title + "." + importMagicExtension;
            }
            outputFilenameSuffix = sanitizeForFilesystem(title);
        }
        //fall back to outputting storage reference + extension
        else if(!Strings.isNullOrEmpty(storageReference)){
            if(!Strings.isNullOrEmpty(importMagicExtension)){
                outputFilenameSuffix = sanitizeForFilesystem(storageReference) + '.' + importMagicExtension;
            }
            else {
                //if we couldn't get extension just output as storage reference
                outputFilenameSuffix = sanitizeForFilesystem(storageReference);
            }
        }
        else if(!Strings.isNullOrEmpty(importMagicExtension)){
            if(!Strings.isNullOrEmpty(reference)){
                outputFilenameSuffix = sanitizeForFilesystem(reference)+ '.' + importMagicExtension;
            }
            else{
                outputFilenameSuffix = importMagicExtension;
            }
        }
        else if(!Strings.isNullOrEmpty(reference)) {
            outputFilenameSuffix = sanitizeForFilesystem(reference);
        }

        if(Strings.isNullOrEmpty(outputFilenameSuffix)){
            return "file";
        }
        return outputPrefix + outputFilenameSuffix;
    }

    /**
     * Gets the name to give the file containing the raw task message as retrieved from the queue.
     * @return Filename for raw task message file.
     */
    public static String getRawTaskMessageFilename(){
        return "raw_"+getTaskMessageFilename();
    }

    /**
     * Gets the name to give the file containing the user-friendly version of the task message.
     * @return Filename for user-friendly task message file.
     */
    public static String getTaskMessageFilename(){
        return "task_message";
    }
}
