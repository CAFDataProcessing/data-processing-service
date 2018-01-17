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
package com.github.cafdataprocessing.utilities.tasksubmitter;

import com.github.cafdataprocessing.utilities.queuehelper.MessageHandler;
import com.github.cafdataprocessing.utilities.queuehelper.QueueConsumerImpl;
import com.github.cafdataprocessing.utilities.queuehelper.QueueManager;
import com.github.cafdataprocessing.utilities.queuehelper.RabbitServices;
import com.github.cafdataprocessing.workflow.constants.WorkflowWorkerConstants;
import com.google.common.base.Strings;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.codec.JsonCodec;
import com.hpe.caf.util.rabbitmq.DefaultRabbitConsumer;
import com.hpe.caf.util.rabbitmq.Event;
import com.hpe.caf.util.rabbitmq.QueueConsumer;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerDocumentTask;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import com.hpe.caf.worker.queue.rabbit.RabbitWorkerQueueConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Tests that the task submitter application successfully submits tasks to a RabbitMQ queue with expected information.
 */
public class MainIT {
    private static final Codec codec = new JsonCodec();
    private List<String> consumerTags;
    private Thread consumerThread;

    @BeforeMethod
    public void intialise(){
        consumerTags =  new ArrayList<>();
    }

    @Test(description = "Test that documents were submitted to the RabbitMQ queue with expected details.")
    public void submitTestDocs() throws IOException, TimeoutException, InterruptedException, CodecException, DataStoreException {
        int expectedNumMessages = 5;
        TestRabbitQueueDetails rabbitQueueDetails = setupQueueConsumer(expectedNumMessages);
        TestMessageListener listener = rabbitQueueDetails.getTestMessageListener();
        CountDownLatch latch = listener.getLatch();
        //give time for messages to be output to queue by task submitter and received by listener
        latch.await(60, TimeUnit.SECONDS);
        List<TaskMessage> messages = listener.getReceivedMessages();

        Assert.assertEquals(messages.size(), expectedNumMessages, "Expecting " + expectedNumMessages + " messages to have been retrieved from the queue.");

        Collection<String> encounteredStorageReferences = new ArrayList<>();
        for(TaskMessage message: messages){
            checkTaskMessage(message, encounteredStorageReferences);
        }
        closeRabbitConnection(rabbitQueueDetails);
    }

    private void closeRabbitConnection(TestRabbitQueueDetails rabbitQueueDetails) throws IOException, TimeoutException {
        Channel consumerChannel = rabbitQueueDetails.getInputChannel();

        if (consumerTags != null) {
            for (String tag : consumerTags) {
                if (tag != null) {
                    consumerChannel.basicCancel(tag);
                }
            }
        }
        rabbitQueueDetails.getConsumer().shutdown();
        consumerChannel.close();
        rabbitQueueDetails.getConnection().close();
    }

    private void checkTaskMessage(TaskMessage message, Collection<String> encounteredStorageReferences) throws CodecException, DataStoreException {
        String expectedDataDir = System.getProperty("CAF_STORAGE_DATA_DIRECTORY");
        if(Strings.isNullOrEmpty(expectedDataDir)){
            throw new RuntimeException("Property CAF_STORAGE_DATA_DIRECTORY was not passed to test as a non-empty value.");
        }
        String projectId = System.getProperty("CAF_TASKSUBMITTER_PROJECTID");
        if(Strings.isNullOrEmpty(projectId)){
            throw new RuntimeException("Property CAF_TASKSUBMITTER_PROJECTID was not passed to test as a non-empty value.");
        }
        Long workflowId = Long.getLong("CAF_TASKSUBMITTER_WORKFLOW_ID");
        if(workflowId==null){
            throw new RuntimeException("Property CAF_TASKSUBMITTER_WORKFLOW_ID was not passed to test as a valid long value.");
        }

        byte[] taskDataBytes = message.getTaskData();
        DocumentWorkerDocumentTask taskData = codec.deserialise(taskDataBytes, DocumentWorkerDocumentTask.class);
        Assert.assertTrue(taskData.customData.containsKey(WorkflowWorkerConstants.CustomData.PROJECT_ID),
                "Custom data on retrieved task data should have a project ID.");
        Assert.assertEquals(taskData.customData.get(WorkflowWorkerConstants.CustomData.PROJECT_ID), projectId,
                "Project ID on task should match that specified in properties.");
        Assert.assertTrue(taskData.customData.containsKey(WorkflowWorkerConstants.CustomData.WORKFLOW_ID),
                "Custom data on retrieved task data should have a workflow ID.");
        Assert.assertEquals(taskData.customData.get(WorkflowWorkerConstants.CustomData.WORKFLOW_ID),
                Long.toString(workflowId), "Workflow ID on task should match that specified in properties.");
        Assert.assertTrue(taskData.customData.containsKey(WorkflowWorkerConstants.CustomData.OUTPUT_PARTIAL_REFERENCE),
                "Custom data on retrieved task data should have an output partial reference.");
        Assert.assertEquals(taskData.customData.get(WorkflowWorkerConstants.CustomData.OUTPUT_PARTIAL_REFERENCE),
                expectedDataDir,
                "Output Partial Reference should match the data directory specified in storage properties.");
        DocumentWorkerDocument document = taskData.document;
        Assert.assertNotNull(document, "Document on task data should not be null.");
        Assert.assertTrue(document.fields.containsKey("storageReference"),
                "Storage reference field should be on document.");

        DocumentWorkerFieldValue storageReferenceValue = document.fields.get("storageReference").iterator().next();
        Assert.assertNotNull(storageReferenceValue, "Storage reference field should not be null.");
        Assert.assertEquals(storageReferenceValue.encoding, DocumentWorkerFieldEncoding.storage_ref,
                "Storage reference field encoding should be expected type.");
        Assert.assertFalse(encounteredStorageReferences.contains(storageReferenceValue.data),
                "Storage reference on the document should not match any other document encountered so far from the queue.");
        encounteredStorageReferences.add(storageReferenceValue.data);
    }

    private TestRabbitQueueDetails setupQueueConsumer(int expectedNumMessages) throws IOException, TimeoutException {
        RabbitWorkerQueueConfiguration configuration = RabbitServices.getInstance().getRabbitQueueConfiguration();
        Connection connection = QueueManager.createRabbitConnection(configuration);

        String inputQueue = RabbitServices.getInstance().getRabbitProperties().getPublishQueue();
        if(Strings.isNullOrEmpty(inputQueue)){
            throw new RuntimeException("No RabbitMQ input queue passed. Check your RabbitMQ properties if this is unexpected.");
        }
        Channel inputChannel = connection.createChannel();
        BlockingQueue<Event<QueueConsumer>> conEvents = new LinkedBlockingQueue<>();

        MessageHandler messageHandler = new MessageHandler();

        TestMessageListener messageListener = new TestMessageListener(configuration.getInputQueue(), expectedNumMessages);
        messageHandler.addListener(messageListener);
        DefaultRabbitConsumer consumer = new DefaultRabbitConsumer(conEvents, new QueueConsumerImpl(inputChannel, messageHandler, codec));

        consumerTags.add(inputChannel.basicConsume(inputQueue, true, consumer));
        consumerThread = new Thread(consumer);
        consumerThread.start();

        return new TestRabbitQueueDetails(messageListener, connection, inputChannel, consumer);
    }
}
