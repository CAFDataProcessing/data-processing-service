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
package com.github.cafdataprocessing.utilities.taskreceiver.taskmessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.cafdataprocessing.entity.fields.FieldChangeRecord;
import com.github.cafdataprocessing.worker.policy.handlers.shared.document.SharedDocument;
import com.google.common.base.Strings;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.hpe.caf.api.worker.TaskMessage;
import com.github.cafdataprocessing.utilities.taskreceiver.services.Services;
import com.github.cafdataprocessing.utilities.taskreceiver.taskoutput.FileNameHelper;
import com.github.cafdataprocessing.utilities.taskreceiver.taskoutput.OutputHelper;
import com.hpe.caf.util.ref.ReferencedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class responsible for output of task messages.
 */
public class TaskMessageWriter {
    private static final Services services = Services.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMessageWriter.class);
    private static final Codec codec;
    private static final ObjectMapper mapper;
    private static final String storageReferenceFieldName = "storageReference";
    private final ManagedDataStore dataStore;
    private final String outputDirectory;
    private final boolean saveTaskDataOnly;
    private final boolean cleanupDataStoreAfterwards;
    private final OutputHelper outputHelper;
    private List<String> storageReferences = new ArrayList<>();

    static {
        codec = services.getCodec();
        mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
    }

    /**
     * Constructs a TaskMessageWriter that will use provided parameters.
     * @param outputDirectory The directory to output task messages to.
     * @param dataStore DataStore to use in retrieving data referenced in task messages.
     * @param outputHelper OutputHelper for task message output operations.
     */
    public TaskMessageWriter (String outputDirectory, ManagedDataStore dataStore, OutputHelper outputHelper, boolean saveTaskDataOnly,
                              boolean cleanupDataStoreAfterwards){
        this.outputDirectory = outputDirectory;
        this.outputHelper = outputHelper;
        this.dataStore = dataStore;
        this.saveTaskDataOnly = saveTaskDataOnly;
        this.cleanupDataStoreAfterwards = cleanupDataStoreAfterwards;
    }

    /**
     * Writes out details about the provided task message.
     * @param taskMessage The task message to output information about.
     * @throws IOException
     * @throws CodecException
     */
    public void writeMessage(TaskMessage taskMessage) throws IOException, CodecException {
        String messageAsString = convertMessageToString(taskMessage);
        LOGGER.debug("Message received converted to string: "+messageAsString);
        writeMessageWithTaskGrouping(taskMessage, messageAsString);
        if(cleanupDataStoreAfterwards) {
            cleanupStoredData();
        }
    }

    private void cleanupStoredData() {
        try {
            for (final String storageReference: storageReferences) {
                dataStore.delete(storageReference);
            }
        } catch (DataStoreException e) {
            e.printStackTrace();
        }
        storageReferences.clear();
    }

    private void writeMessageWithTaskGrouping(TaskMessage taskMessage, String messageAsStr) throws IOException, CodecException {
        //if the task data represents type 'SharedDocument' then additional files should be output
        SharedDocument document = null;
        try{
            document = codec.deserialise(taskMessage.getTaskData(), SharedDocument.class);
        }
        catch(CodecException e){
            LOGGER.debug("Error deserializing task data to SharedDocument. Avoiding output of document specific files.");
        }
        String folderName = null;
        if(document!=null) {
            folderName = outputHelper.createFolderFromDocument(document, outputDirectory);
        }
        else{
            folderName = outputHelper.createFolderFromTaskMessageId(taskMessage, outputDirectory);
        }

        writeMessageDetails(taskMessage, messageAsStr, folderName);
        if(document!=null){
            writeSharedDocumentDetails(taskMessage, messageAsStr, document, folderName);
        }
    }

    private void writeMessageDetails(TaskMessage taskMessage, String messageAsStr, String messageOutputDir) throws IOException, CodecException {
        writeFriendlyMessageFile(messageAsStr, messageOutputDir);
        writeRawMessageFile(taskMessage, messageOutputDir);
    }

    private void writeSharedDocumentDetails(TaskMessage taskMessage, String messageAsStr,
                                            SharedDocument document, String messageOutputDir) throws IOException, CodecException {
        writeFriendlyMessageFile(messageAsStr, messageOutputDir);
        writeRawMessageFile(taskMessage, messageOutputDir);
        //write out the binary file representation
        String storageReference = null;
        try {
            storageReference = getStorageReferenceFromDocument(document);
            storageReferences.add(storageReference);
        } catch (Exception e) {
            LOGGER.error("Cannot write file to disk. Unable to find storage reference on document in task message in folder: "+
                    messageOutputDir);
        }
        writeStoredDocumentFile(document, storageReference, messageOutputDir);
        //write out all metadata reference fields as files
        writeMetadataReferences(document, messageOutputDir);
    }

    private void writeMetadataReference(SharedDocument document, String messageOutputDir,
                                               Map.Entry<String, ReferencedData> metadataReference) throws IOException {
        //retrieve the file
        String metadataReferenceValue = metadataReference.getValue().getReference();
        storageReferences.add(metadataReferenceValue);
        InputStream rawFile = null;
        try {
            rawFile = dataStore.retrieve(metadataReferenceValue);
        } catch (DataStoreException e) {
            LOGGER.error("Cannot write file to disk. Unable to retrieve file from storage. Folder: "
                    +messageOutputDir+", reference: "+metadataReferenceValue);
            return;
        }
        outputHelper.outputStream(messageOutputDir, FileNameHelper.getMetadataRefOutputFilename(metadataReference),
                rawFile);
    }

    private void writeMetadataReferences(SharedDocument document, String messageOutputDir) throws IOException {
        for(Map.Entry<String, ReferencedData> entry: document.getMetadataReference()){
            //only write those that are storage references to files
            if(!Strings.isNullOrEmpty(entry.getValue().getReference())) {
                writeMetadataReference(document, messageOutputDir, entry);
            }
        }
    }

    private void writeFriendlyMessageFile(String messageAsStr, String messageOutputDir) throws IOException {
        if (saveTaskDataOnly) {
            JsonNode node = mapper.readTree(messageAsStr);
            if (node.has(TaskMessageConstants.TASK_DATA)) {
                messageAsStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node.get(TaskMessageConstants.TASK_DATA));
            }
            else{
                LOGGER.error("Output message does not contain node: " + TaskMessageConstants.TASK_DATA + ". It cannot be output separately");
            }
        }
        outputHelper.outputString(messageOutputDir, FileNameHelper.getTaskMessageFilename(), messageAsStr);
    }

    private void writeRawMessageFile(TaskMessage taskMessage, String messageOutputDir) throws IOException, CodecException {
        if (saveTaskDataOnly) {
            outputHelper.outputString(messageOutputDir, FileNameHelper.getRawTaskMessageFilename(), org.apache.commons.io.IOUtils.toString(codec.serialise(taskMessage.getTaskData())));
        } else {
            outputHelper.outputString(messageOutputDir, FileNameHelper.getRawTaskMessageFilename(), org.apache.commons.io.IOUtils.toString(codec.serialise(taskMessage)));
        }
    }

    private void writeStoredDocumentFile(SharedDocument document, String storageReference,
                                                String messageOutputDir) throws IOException {
        //retrieve the file
        InputStream rawFile = null;
        try {
            rawFile = dataStore.retrieve(storageReference);
        } catch (DataStoreException e) {
            LOGGER.error("Cannot write file to disk. Unable to retrieve file from storage. Folder: "
                    +messageOutputDir);
            return;
        }
        outputHelper.outputStream(messageOutputDir, FileNameHelper.getDocumentOutputFilename(document),
                rawFile);
    }

    private String getStorageReferenceFromDocument(SharedDocument document) throws Exception {
        String storageReference = null;
        Optional<String> storageReferenceOpt = document.getMetadata().stream().filter(
                md -> md.getKey().equals(storageReferenceFieldName)).map(md -> md.getValue())
                .findFirst();

        if(!storageReferenceOpt.isPresent() || Strings.isNullOrEmpty(storageReferenceOpt.get())) {
            // Try get the storageReference off the DocumentProcessingRecord
            final Map<String, FieldChangeRecord> processingRecordMetadataChanges = document.getDocumentProcessingRecord().metadataChanges;
            if (processingRecordMetadataChanges.containsKey(storageReferenceFieldName)) {
                storageReference = processingRecordMetadataChanges.get(storageReferenceFieldName).changeValues.iterator().next().value;
            }
        } else {
            storageReference = storageReferenceOpt.get();
        }

        //If null or empty, check if the storageReference is in the DocumentProcessingRecord.
        if(storageReference == null) {
                throw new Exception("No value present on " + storageReferenceFieldName + " field for document to use in constructing folder name for output.");
        }
        return storageReference;
    }

    private String convertMessageToString( TaskMessage taskMessage ) throws JsonProcessingException {
        JsonNode node = mapper.valueToTree( taskMessage );
        // Attempt to decode from the taskMessage, a node called taskData.
        // If it is present, we wish to base64 decode it, and put the real object back in its place.
        // So the user of this application can see what is within this node easily!
        TaskDataDecoder.decodeTaskData(node, mapper);

        //decode context if applicable
        if ( node.has( TaskMessageConstants.CONTEXT ) )
        {
            JsonNode context = node.get(  TaskMessageConstants.CONTEXT);
            if ( context != null )
            {
                Iterator<String> it = node.get(  TaskMessageConstants.CONTEXT ).fieldNames();
                while ( it.hasNext() )
                {
                    String currentField = it.next();

                    // attempt to decode each child field of context, if we can
                    TaskDataDecoder.attemptToDecodeNode(context, mapper, currentField );
                }
            }
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( node );
        } catch (JsonProcessingException e) {
            LOGGER.debug("Failure trying to convert task message to text.", e);
            throw e;
        }
    }
}
