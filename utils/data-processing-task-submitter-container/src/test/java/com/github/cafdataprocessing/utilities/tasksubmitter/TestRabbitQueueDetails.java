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

import com.hpe.caf.util.rabbitmq.DefaultRabbitConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Object to contain relevant RabbitMQ connection information for tests.
 */
public class TestRabbitQueueDetails {
    private final TestMessageListener testMessageListener;
    private final Connection connection;
    private final Channel inputChannel;
    private final DefaultRabbitConsumer consumer;

    public TestRabbitQueueDetails(TestMessageListener testMessageListener, Connection connection,
                                  Channel inputChannel, DefaultRabbitConsumer consumer){
        this.testMessageListener = testMessageListener;
        this.connection = connection;
        this.inputChannel = inputChannel;
        this.consumer = consumer;
    }

    public DefaultRabbitConsumer getConsumer(){
        return this.consumer;
    }

    public TestMessageListener getTestMessageListener(){
        return this.testMessageListener;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public Channel getInputChannel(){
        return this.inputChannel;
    }
}
