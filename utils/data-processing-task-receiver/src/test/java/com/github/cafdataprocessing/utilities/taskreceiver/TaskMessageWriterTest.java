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
package com.github.cafdataprocessing.utilities.taskreceiver;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.cafdataprocessing.worker.policy.handlers.shared.document.SharedDocument;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.*;
import com.hpe.caf.codec.JsonCodec;
import com.github.cafdataprocessing.utilities.taskreceiver.services.Services;
import com.github.cafdataprocessing.utilities.taskreceiver.taskmessage.TaskMessageWriter;
import com.github.cafdataprocessing.utilities.taskreceiver.taskoutput.FileNameHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Tests that task messages are correctly written.
 */
public class TaskMessageWriterTest {
    private static final String storeOutputLocation = Services.getInstance().getStorageProperties().getDataDirectory();
    private TestDocumentData testDocumentData;

    private static final ManagedDataStore dataStore = MockedDataStore.getDataStore();

    private MockedOutputHelper mockedOutputHelper;

    @BeforeMethod(description = "Stores the doc contents for use in testing.")
    public void setupLocalStorage() throws IOException, DataStoreException {
        this.testDocumentData = new TestDocumentData(dataStore, storeOutputLocation);
        this.testDocumentData.storeAllDocs();
        mockedOutputHelper = new MockedOutputHelper();
    }

    @AfterMethod
    public void postTestActions() throws DataStoreException {
        cleanupStoredData();
        TestTaskMessageBuilder.incrementMessageCounter();
    }

    public void cleanupStoredData() throws DataStoreException {
        this.testDocumentData.cleanupStoredData();
    }

    @Test()
    public void writeDocxTest() throws Exception {
        String outputDir = "./test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        String docRef = "/input/Good team.docx";
        SharedDocument docxExample = testDocumentData.getDocxExample(docRef);
        TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(docxExample);
        writer.writeMessage(message);
    }

    @Test()
    public void writeJpgTest() throws Exception {
        String outputDir = "/test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        String docRef = "/input/other - team lineup.jpg";
        SharedDocument document = testDocumentData.getJpgExample(docRef);
        TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(document);
        writer.writeMessage(message);
    }

    @Test
    public void writeZipTest() throws Exception {
        String outputDir = "/test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        //write the root document first
        SharedDocument rootDocument = testDocumentData.getZipRootExample();
        TaskMessage rootMessage = TestTaskMessageBuilder.buildTaskMessage(rootDocument);
        writer.writeMessage(rootMessage);

        //write level 2 sub-file document, testing that if parent folder doesn't exist it is correctly created
        SharedDocument level2Document = testDocumentData.getLevel2DocExample();
        String subDocTaskId = rootMessage.getTaskId() + ":1:0";
        TaskMessage level2DocMessage = TestTaskMessageBuilder.buildTaskMessage(level2Document, subDocTaskId);
        writer.writeMessage(level2DocMessage);

        //then write the parent of the level 2 sub-file to the directory that should already exist
        SharedDocument level2Zip = testDocumentData.getZipLevel2Example();
        subDocTaskId = rootMessage.getTaskId() + ":1";
        TaskMessage level2ZipMessage = TestTaskMessageBuilder.buildTaskMessage(level2Zip, subDocTaskId);
        writer.writeMessage(level2ZipMessage);

        //write the document at level 1 of zip
        SharedDocument level1Document = testDocumentData.getZipLevel1DocExample();
        subDocTaskId = rootMessage.getTaskId() + ":0";
        TaskMessage level1DocMessage = TestTaskMessageBuilder.buildTaskMessage(level1Document, subDocTaskId);
        writer.writeMessage(level1DocMessage);
    }

    @Test()
    public void writeDocxWithStorageRefNotInMetadataTest() throws Exception {
        String outputDir = "./test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        String docRef = "/input/Good team.docx";
        SharedDocument docxExample = testDocumentData.getDocxExample(docRef);

        // Clear the metadata, so that the DocumentProcessingRecord is checked for a storageReference
        docxExample.getMetadata().clear();
        TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(docxExample);
        writer.writeMessage(message);

        Assert.assertEquals(mockedOutputHelper.getLocalStorage().size(), 4, "Four files should be output to local storage.");
    }

    @Test(expectedExceptions = DataStoreException.class)
    public void writeDocxAndEnsureDataStoreCleared() throws Exception {
        final String outputDir = "./test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = true;
        final TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        final String docRef = "/input/Good team.docx";
        final SharedDocument docxExample = testDocumentData.getDocxExample(docRef);

        // Get the storageReference odd the document so that it can be validated that it has been deleted later.
        final String storageRef = docxExample.getMetadata().stream().filter(
                md -> md.getKey().equals("storageReference")).map(md -> md.getValue())
                .findFirst().get();
        final TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(docxExample);
        writer.writeMessage(message);

        // Throws a DataStoreException because the reference should not exist.
        final InputStream stream = dataStore.retrieve(storageRef);
    }

    @Test
    public void writeNonSharedDocumentMessage() throws IOException, CodecException {
        String outputDir = "/test";
        boolean saveTaskDataOnly = false;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        String expectedOutputData = "This is the output message expected.";
        Codec codec = new JsonCodec();
        TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(codec.serialise(expectedOutputData));
        writer.writeMessage(message);

        HashMap<String, InputStream> localStorage = mockedOutputHelper.getLocalStorage();
        Assert.assertEquals(localStorage.size(), 2, "Expecting two files to have been output for non shared document task message.");

        String expectedFolder = buildTestOutputFolderName(message, outputDir);
        InputStream rawTaskMessageStream = localStorage.get(expectedFolder + FileNameHelper.getRawTaskMessageFilename());
        TaskMessage outputRawTaskMessage = codec.deserialise(rawTaskMessageStream, TaskMessage.class);
        Assert.assertEquals(outputRawTaskMessage.getTaskId(), message.getTaskId(), "Output raw message should have expected task ID.");

        String receivedOutputData = codec.deserialise(outputRawTaskMessage.getTaskData(), String.class);
        Assert.assertEquals(receivedOutputData, expectedOutputData, "Output raw data should match expected data.");

        InputStream prettyTaskMessageStream = localStorage.get(expectedFolder + FileNameHelper.getTaskMessageFilename());
        JsonNode prettyTaskMessageNode = codec.deserialise(prettyTaskMessageStream, JsonNode.class);
        JsonNode taskDataNode = prettyTaskMessageNode.findValue("taskData");
        String receivedPrettyOutputData = taskDataNode.textValue();
        Assert.assertEquals(receivedPrettyOutputData, expectedOutputData, "Output formatted data should match expected data.");
    }

    @Test
    public void writeNonSharedDocumentTaskDataOnlyMessage() throws IOException, CodecException {
        String outputDir = "/test";
        boolean saveTaskDataOnly = true;
        boolean cleanDataStoreAfterProcessing = false;
        TaskMessageWriter writer = new TaskMessageWriter(
                outputDir, dataStore, mockedOutputHelper.getOutputHelper(), saveTaskDataOnly, cleanDataStoreAfterProcessing);
        String expectedOutputData = "This is the output message expected.";
        Codec codec = new JsonCodec();
        TaskMessage message = TestTaskMessageBuilder.buildTaskMessage(codec.serialise(expectedOutputData));
        writer.writeMessage(message);

        HashMap<String, InputStream> localStorage = mockedOutputHelper.getLocalStorage();
        Assert.assertEquals(localStorage.size(), 2, "Expecting two files to have been output for non shared document task message.");

        String expectedFolder = buildTestOutputFolderName(message, outputDir);
        InputStream rawTaskMessageStream = localStorage.get(expectedFolder + FileNameHelper.getRawTaskMessageFilename());
        String outputRawTaskMessage = IOUtils.toString(rawTaskMessageStream, StandardCharsets.UTF_8);
        //Base64 Decode the result and remove the outer quotes.
        String decodedOutputRawTaskMessage = new String(Base64.decodeBase64(outputRawTaskMessage), "UTF-8").replaceAll("^\"|\"$", "");
        Assert.assertEquals(decodedOutputRawTaskMessage, expectedOutputData, "Output raw data should match expected data.");

        InputStream prettyTaskMessageStream = localStorage.get(expectedFolder + FileNameHelper.getTaskMessageFilename());
        String prettyTaskMessageNode = IOUtils.toString(prettyTaskMessageStream, StandardCharsets.UTF_8).replaceAll("^\"|\"$", "");
        Assert.assertEquals(prettyTaskMessageNode, expectedOutputData, "Output formatted data should match expected data.");
    }

    private static String buildTestOutputFolderName(TaskMessage message, String outputDir){
        File expectedPath = new File(outputDir);
        StringBuilder expectedFolderBuilder = new StringBuilder();
        expectedFolderBuilder.append(expectedPath.getAbsolutePath());
        expectedFolderBuilder.append(File.separator);
        expectedFolderBuilder.append(FileNameHelper.sanitizeForFilesystem(message.getTaskId()));
        expectedFolderBuilder.append(File.separator);
        return expectedFolderBuilder.toString();
    }
}
