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

import com.hpe.caf.api.worker.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Notifies all registered MessageListeners of a new message received.
 */
public class MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    private List<MessageListener> listeners = new ArrayList<>();

    /**
     * Notify all registered MessageListeners of received message.
     *
     * @param message A TaskMessage to pass to the listeners
     * @param queue Specifies the queue that the listener should be listening to for message to be forwarded to listener.
     */
    public void handleMessage(TaskMessage message, String queue) {
        for (MessageListener listener : listeners) {
            if (listener.getQueues().contains(queue) || listener.getQueues().isEmpty()) {
                try {
                    listener.messageReceived(message);
                }
                catch (Exception e) {
                    //individual listener failure should not stop the message being sent to other listeners.
                    LOGGER.error("Error encountered on listener when trying to handle message.", e);
                }
            }
        }
    }

    /**
     * Register a new Listener
     *
     * @param listener the MessageListener to register
     */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }
}
