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
package com.github.cafdataprocessing.utilities.familytasksubmitter.taskmessage;

import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterConstants;
import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterProperties;
import com.hpe.caf.api.*;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.codec.*;
import com.hpe.caf.util.rabbitmq.*;
import com.rabbitmq.client.*;
import org.slf4j.*;

import java.io.*;
import java.util.concurrent.*;

/**
 * A message dispatcher for sending messages to a RabbitMQ queue.
 */
public class RabbitMessageDispatcher implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMessageDispatcher.class);
    private static final Codec codec = new JsonCodec();
    private Channel channel;
    private Connection conn;
    private String outputQueueName;

    public void configure(final String outputQueueName) throws IOException, TimeoutException
    {
        try {
            this.conn = RabbitUtil.createRabbitConnection(
                    FamilyTaskSubmitterProperties.getRabbitHost(),
                    FamilyTaskSubmitterProperties.getRabbitPort(),
                    FamilyTaskSubmitterProperties.getRabbitUsername(),
                    FamilyTaskSubmitterProperties.getRabbitPassword());
            this.channel = conn.createChannel();
            this.outputQueueName = outputQueueName;
            //RabbitUtil.declareWorkerQueue(channel, outputQueueName);
        } catch (IOException | TimeoutException ex) {
            LOG.error("An error occured establishing a connection with RabbitMq", ex);
            throw ex;
        }
    }

    public void sendMessage(final TaskMessage taskMessage) throws CodecException, IOException
    {
        channel.basicPublish("", outputQueueName, MessageProperties.PERSISTENT_TEXT_PLAIN, codec.serialise(taskMessage));
    }

    /**
     * Resets the RabbitMessageDispatcher for re-use. This method will close the object and nullify all fields.
     *
     * @throws IOException If closing fails.
     */
    public void reset() throws IOException
    {
        close();
        channel = null;
        conn = null;
        outputQueueName = null;
    }

    @Override
    public void close() throws IOException
    {
        if (channel != null) {
            try {
                channel.close();
            } catch (TimeoutException | IOException e) {
                LOG.warn("Failed to close channel, ignoring", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                LOG.warn("Failed to close connection, ignoring", e);
            }
        }
    }
}
