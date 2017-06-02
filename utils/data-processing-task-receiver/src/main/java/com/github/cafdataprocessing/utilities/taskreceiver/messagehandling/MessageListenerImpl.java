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
package com.github.cafdataprocessing.utilities.taskreceiver.messagehandling;

import com.github.cafdataprocessing.utilities.queuehelper.MessageListener;
import com.github.cafdataprocessing.utilities.queuehelper.RabbitServices;
import com.github.cafdataprocessing.utilities.taskreceiver.services.Services;
import com.github.cafdataprocessing.utilities.taskreceiver.taskmessage.TaskMessageWriter;
import com.github.cafdataprocessing.utilities.taskreceiver.taskoutput.OutputHelper;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.hpe.caf.api.worker.TaskMessage;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Message listener implementation that sends received message to task writer so it may be output to disk.
 */

public class MessageListenerImpl implements MessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerImpl.class);
    private final List<String> queuesToListenOn;
    private final TaskMessageWriter messageWriter;

    public MessageListenerImpl()
    {
        this.queuesToListenOn = RabbitServices.getInstance().getRabbitProperties().getConsumeQueueNames();
        String messageOutputDir = Services.getInstance().getTaskReceiverProperties().getOutputDirectory();
        Boolean saveTaskDataOnly = Boolean.valueOf(Services.getInstance().getTaskReceiverProperties().getIsSaveTaskDataOnly());
        Boolean cleanupDataStoreAfterProcessing = Boolean.valueOf(Services.getInstance().getTaskReceiverProperties().getCleanupDataStoreAfterProcessing());
        ManagedDataStore dataStore = Services.getInstance().getStorageDataStore();
        OutputHelper outputHelper = new OutputHelper();
        messageWriter = new TaskMessageWriter(messageOutputDir, dataStore, outputHelper, saveTaskDataOnly, cleanupDataStoreAfterProcessing);
    }

    @Override
    public void messageReceived( TaskMessage taskMessage )
    {
        LOGGER.info("New message received, ID: " +taskMessage.getTaskId());
        try
        {
            messageWriter.writeMessage(taskMessage);
            LOGGER.info("Output task message with ID: "+taskMessage.getTaskId());
        }
        catch ( IOException | CodecException e )
        {
            LOGGER.error("Failure while handling received message, ID: "+taskMessage.getTaskId(), e);
        }
    }

    @Override
    public List<String> getQueues()
    {
        return queuesToListenOn;
    }
}
