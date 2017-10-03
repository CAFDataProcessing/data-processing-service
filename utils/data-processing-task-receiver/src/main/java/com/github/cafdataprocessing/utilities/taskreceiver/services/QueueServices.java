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
package com.github.cafdataprocessing.utilities.taskreceiver.services;

import com.github.cafdataprocessing.utilities.queuehelper.MessageHandler;
import com.github.cafdataprocessing.utilities.queuehelper.MessageListener;
import com.github.cafdataprocessing.utilities.queuehelper.QueueManager;
import com.github.cafdataprocessing.utilities.queuehelper.RabbitServices;

import java.io.IOException;

/**
 * Initialises objects for communication with RabbitMQ and provides access to receive tasks from the queue.
 */
public class QueueServices {
    static final QueueManager queueManager;

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

    public static void addMessageListener(MessageListener listener){
        MessageHandler handler = queueManager.getMessageHandler();
        handler.addListener(listener);
    }

    public static void shutdown() throws IOException {
        queueManager.close();
    }
}
