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
package com.github.cafdataprocessing.utilities.queuehelper;

import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.util.rabbitmq.Delivery;
import com.hpe.caf.util.rabbitmq.QueueConsumer;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Implementation of the QueueConsumer interface to read messages from a rabbitmq queue.
 */
public class QueueConsumerImpl implements QueueConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConsumerImpl.class);

    private final Channel channel;
    private final MessageHandler messageHandler;
    private final Codec codec;
    private static final Object syncLock = new Object();

    public QueueConsumerImpl(Channel channel, MessageHandler messageHandler, Codec codec) {
        this.channel = channel;
        this.messageHandler = messageHandler;
        this.codec = codec;
    }

    /**
     * Handles a delivery and passes to message handler.
     *
     * @param delivery the newly arrived message including metadata
     */
    @Override
    public void processDelivery(Delivery delivery) {
        try {

            TaskMessage taskMessage = codec.deserialise(delivery.getMessageData(), TaskMessage.class);
            synchronized (syncLock) {
                messageHandler.handleMessage(taskMessage,delivery.getEnvelope().getRoutingKey());
            }
        } catch (CodecException e) {
            LOGGER.error("Error encountered trying to process delivery from queue.", e);
        }
    }

    /**
     * Acknowledge a message
     *
     * @param tag the RabbitMQ id of the message to acknowledge
     */
    @Override
    public void processAck(long tag) {
        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processReject(long tag) {

    }

    @Override
    public void processDrop(long tag) {

    }
}
