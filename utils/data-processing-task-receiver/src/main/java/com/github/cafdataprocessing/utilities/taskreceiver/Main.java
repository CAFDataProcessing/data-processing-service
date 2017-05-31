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

import com.github.cafdataprocessing.utilities.taskreceiver.services.QueueServices;
import com.github.cafdataprocessing.utilities.taskreceiver.messagehandling.MessageListenerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Entry point for running the task receiver application.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        (new Main()).run();
    }

    public void run(){
        LOGGER.info("Starting task-receiver.");
        //check that this is a valid environment to run application in
        ValidateEnvironment.validate();
        //setup rabbit message listener to output received messages
        MessageListenerImpl listener = new MessageListenerImpl();
        QueueServices.addMessageListener(listener);
        LOGGER.info("Listening for messages to output.");
    }
}
