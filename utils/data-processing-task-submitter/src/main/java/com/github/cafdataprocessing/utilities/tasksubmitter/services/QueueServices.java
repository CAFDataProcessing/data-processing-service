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
package com.github.cafdataprocessing.utilities.tasksubmitter.services;

import com.github.cafdataprocessing.utilities.queuehelper.QueueManager;
import com.github.cafdataprocessing.utilities.queuehelper.RabbitServices;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage.FileAndTaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Initialises objects for communication with RabbitMQ and provides access to submit tasks to the queue through these.
 */
public class QueueServices {
    private static QueueManager queueManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueServices.class);

    static {
        try
        {
            queueManager = QueueManager.getInstance();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to create connection to RabbitHost: \""
                    + RabbitServices.getInstance().getRabbitProperties().getHost() + "\" at port: \""
                    + RabbitServices.getInstance().getRabbitProperties().getPort()
                    + "\"."
                    + "Check your properties are correct, and that rabbit is running.", e );
        }
    }

    public static void publishAllMessages( List<FileAndTaskMessage> taskMessages ) throws IOException, CodecException
    {
        String batchId = UUID.randomUUID().toString();
        LOGGER.debug("Sending " + taskMessages.size() +
                " messages to the Workflow Worker Input Queue for Data Processing. Batch ID:" +batchId);
        for ( FileAndTaskMessage taskMessage : taskMessages )
        {
            publishMessage(taskMessage);
        }
        LOGGER.debug("Message batch sent. ID: "+batchId);
    }

    public static void publishMessage(FileAndTaskMessage fileWithTaskMessage) throws CodecException, IOException {
        TaskMessage taskMessage = fileWithTaskMessage.getTaskMessage();
        LOGGER.debug("Sending task message with task ID: "+taskMessage.getTaskId());
        queueManager.publishMessage(taskMessage);
        LOGGER.debug("Sent task message with task ID: "+taskMessage.getTaskId());
    }

    public static void shutdown() throws IOException {
        queueManager.close();
    }
}
