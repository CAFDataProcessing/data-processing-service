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
package com.github.cafdataprocessing.utilities.tasksubmitter;

import com.github.cafdataprocessing.utilities.queuehelper.MessageListener;
import com.hpe.caf.api.worker.TaskMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Message listener for use with integration tests.
 */
public class TestMessageListener implements MessageListener {
    private List<String> queuesToListenOn = new ArrayList<>();
    //Latch to signal that a message has been received.
    private final CountDownLatch latch;
    private final List<TaskMessage> receivedMessages = new ArrayList<>();

    public TestMessageListener(String queueToListenTo, int expectedNumMessages){
        queuesToListenOn.add(queueToListenTo);
        this.latch = new CountDownLatch(expectedNumMessages);
    }

    public TestMessageListener(List<String> queuesToListenTo, int expectedNumMessages){
        queuesToListenOn = queuesToListenTo;
        this.latch = new CountDownLatch(expectedNumMessages);
    }

    @Override
    public void messageReceived(TaskMessage taskMessage) {
        receivedMessages.add(taskMessage);
        latch.countDown();
    }

    public CountDownLatch getLatch(){
        return latch;
    }

    public List<TaskMessage> getReceivedMessages(){
        return receivedMessages;
    }

    @Override
    public List<String> getQueues() {
        return queuesToListenOn;
    }
}
