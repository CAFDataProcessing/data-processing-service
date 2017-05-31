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
package com.github.cafdataprocessing.utilities.queuehelper;

import com.google.common.base.Strings;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.worker.TaskMessage;
import com.hpe.caf.codec.JsonCodec;
import com.hpe.caf.configs.RabbitConfiguration;
import com.hpe.caf.util.rabbitmq.DefaultRabbitConsumer;
import com.hpe.caf.util.rabbitmq.Event;
import com.hpe.caf.util.rabbitmq.QueueConsumer;
import com.hpe.caf.util.rabbitmq.RabbitUtil;
import com.hpe.caf.worker.queue.rabbit.RabbitWorkerQueueConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Utility Class that manages the connection to the rabbit server, QueueConsumer thread and publishing new tasks
 */
public class QueueManager implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueManager.class);

    private static QueueManager instance;
    private final DefaultRabbitConsumer consumer;
    private final Connection connection;
    private final Channel publisherChannel;
    private final Channel consumerChannel;
    private final RabbitServices services = RabbitServices.getInstance();
    private final List<String> consumerTags = new ArrayList<>();
    private final Thread consumerThread;

    private final String publishQueue;
    private final List<String> consumeQueues;
    private final MessageHandler messageHandler = new MessageHandler();

    private final Codec codec = new JsonCodec();

    /**
     * Constructs or returns the QueueManager instance
     *
     * @return the instance of QueueManager
     * @throws Exception
     */
    public static QueueManager getInstance() throws Exception {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    /**
     * Private constructor for Singleton pattern
     *
     * @throws Exception
     */
    private QueueManager() throws Exception {
        RabbitWorkerQueueConfiguration configuration = RabbitServices.getInstance().getRabbitQueueConfiguration();
        connection = createRabbitConnection(configuration);

        publishQueue = services.getRabbitProperties().getPublishQueue();
        consumeQueues = services.getRabbitProperties().getConsumeQueueNames();
        if(Strings.isNullOrEmpty(publishQueue)){
            LOGGER.debug("No RabbitMQ queue to publish to passed. Check your RabbitMQ properties if this is unexpected.");
            publisherChannel = null;
        }
        else{
            publisherChannel = connection.createChannel();
            RabbitUtil.declareWorkerQueue(publisherChannel, publishQueue);
        }
        if(consumeQueues==null || consumeQueues.isEmpty()){
            LOGGER.debug("No RabbitMQ queues to consume message from set. Check your RabbitMQ properties if this is unexpected.");
            consumerChannel = null;
            consumer = null;
            consumerThread = null;
        }
        else{
            consumerChannel = connection.createChannel();
            for (String queue : consumeQueues) {
                RabbitUtil.declareWorkerQueue(consumerChannel, queue);
            }
            BlockingQueue<Event<QueueConsumer>> conEvents = new LinkedBlockingQueue<>();

            consumer = new DefaultRabbitConsumer(conEvents, new QueueConsumerImpl(consumerChannel, messageHandler, codec));
            for (String queue : consumeQueues) {
                consumerTags.add(consumerChannel.basicConsume(queue, true, consumer));
            }
            consumerThread = new Thread(consumer);
            consumerThread.start();
        }
    }

    /**
     * Publishes a TaskMessage to the InputQueue
     *
     * @param message the message to publish
     * @throws CodecException if TaskMessage fails to serialize
     * @throws IOException    if channel fails to publish the message
     */
    public void publishMessage(TaskMessage message) throws CodecException, IOException {
        if(Strings.isNullOrEmpty(publishQueue)){
            LOGGER.warn("Attempted to publish message when publish queue is not set. Message not published.");
            return;
        }

        LOGGER.info("Publishing task message " + message.getTaskId());
        byte[] data = codec.serialise(message);
        publisherChannel.basicPublish("", publishQueue, MessageProperties.TEXT_PLAIN, data);
    }


    /**
     * Closes all open channels and connections to Rabbitmq server and stops the Consumer thread.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (consumerTags != null) {
            for (String tag : consumerTags) {
                if (tag != null) {
                    consumerChannel.basicCancel(tag);
                }
            }
        }
        if (consumer != null) {
            consumer.shutdown();
        }
        if (consumerChannel != null) {
            try {
                consumerChannel.close();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (publisherChannel != null) {
            try {
                publisherChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method to establish connection to Rabbitmq Server
     *
     * @param configuration the Connection configuration
     * @return Rabbitmq connection
     * @throws IOException
     * @throws TimeoutException
     */
    public static Connection createRabbitConnection(RabbitWorkerQueueConfiguration configuration) throws IOException, TimeoutException {
        RabbitConfiguration rc = configuration.getRabbitConfiguration();
        ConnectionOptions lyraOpts = RabbitUtil.createLyraConnectionOptions(rc.getRabbitHost(), rc.getRabbitPort(), rc.getRabbitUser(), rc.getRabbitPassword());
        Config lyraConfig = RabbitUtil.createLyraConfig(rc.getBackoffInterval(), rc.getMaxBackoffInterval(), rc.getMaxAttempts());
        Connection connection = RabbitUtil.createRabbitConnection(lyraOpts, lyraConfig);
        return connection;
    }

    /**
     * Returns the messageHandler property
     *
     * @return the messageHandler
     */
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
