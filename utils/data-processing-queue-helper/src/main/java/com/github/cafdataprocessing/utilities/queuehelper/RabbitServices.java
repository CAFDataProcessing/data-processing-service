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

import com.hpe.caf.configs.RabbitConfiguration;
import com.hpe.caf.worker.queue.rabbit.RabbitWorkerQueueConfiguration;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Provides access to rabbit configuration read from environment.
 */
public class RabbitServices {
    private static RabbitServices instance;

    private final RabbitProperties rabbitProperties;
    private final RabbitWorkerQueueConfiguration queueConfiguration;

    /**
     * Provides access to the single instance of RabbitServices in application.
     * @return
     */
    public static RabbitServices getInstance() {
        if (instance == null) {
            instance = new RabbitServices();
        }
        return instance;
    }

    private RabbitServices() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBeanDefinition("RabbitProperties", new RootBeanDefinition(RabbitProperties.class));
        context.refresh();
        rabbitProperties = context.getBean(RabbitProperties.class);
        queueConfiguration = buildQueueConfig();
    }

    public RabbitProperties getRabbitProperties(){
        return rabbitProperties;
    }

    public RabbitWorkerQueueConfiguration getRabbitQueueConfiguration(){
        return queueConfiguration;
    }

    /**
     * Read in queue configuration from properties.
     *
     * @return RabbitWorkerQueueConfiguration
     */
    private RabbitWorkerQueueConfiguration buildQueueConfig(){
        RabbitWorkerQueueConfiguration localQueueConfiguration = new RabbitWorkerQueueConfiguration();
        localQueueConfiguration.setInputQueue(rabbitProperties.getPublishQueue());
        localQueueConfiguration.setRetryLimit(0);
        localQueueConfiguration.setRetryQueue(rabbitProperties.getPublishQueue());
        RabbitConfiguration rabbitConfiguration = new RabbitConfiguration();
        rabbitConfiguration.setBackoffInterval(rabbitProperties.getBackOffInterval());
        rabbitConfiguration.setMaxAttempts(rabbitProperties.getMaxRetryAttempts());
        rabbitConfiguration.setMaxBackoffInterval(rabbitProperties.getMaxBackoff());
        rabbitConfiguration.setRabbitHost(rabbitProperties.getHost());
        rabbitConfiguration.setRabbitPort(rabbitProperties.getPort());
        rabbitConfiguration.setRabbitUser(rabbitProperties.getUser());
        rabbitConfiguration.setRabbitPassword(rabbitProperties.getPassword());
        localQueueConfiguration.setRabbitConfiguration(rabbitConfiguration);
        return localQueueConfiguration;
    }
}
