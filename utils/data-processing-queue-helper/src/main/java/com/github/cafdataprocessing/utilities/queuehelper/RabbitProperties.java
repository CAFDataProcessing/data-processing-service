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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * Properties describing connection to RabbitMQ.
 */
@Configuration
@PropertySource("classpath:rabbit.properties")
@PropertySource(value = "file:${CAF_CONFIG_LOCATION}/rabbit.properties", ignoreResourceNotFound = true)
public class RabbitProperties {
    @Autowired
    private Environment environment;

    public Integer getBackOffInterval() {
        return Integer.valueOf(environment.getProperty("CAF_RABBITMQ_BACKOFF_INTERVAL"));
    }

    public String getHost() {
        return environment.getProperty("CAF_RABBITMQ_HOST");
    }

    public String getPublishQueue() {
        return environment.getProperty("CAF_RABBITMQ_PUBLISH_QUEUE");
    }

    public Integer getMaxBackoff() {
        return Integer.valueOf(environment.getProperty("CAF_RABBITMQ_MAX_BACKOFF_INTERVAL"));
    }

    public Integer getMaxRetryAttempts() {
        return Integer.valueOf(environment.getProperty("CAF_RABBITMQ_MAX_ATTEMPTS"));
    }

    public List<String> getConsumeQueueNames() {
        String consumeQueuesAsStr = environment.getProperty("CAF_RABBITMQ_CONSUME_QUEUES");
        if(Strings.isNullOrEmpty(consumeQueuesAsStr)){
            return null;
        }
        String[] queues = consumeQueuesAsStr.split(",");
        return Arrays.asList(queues);
    }
    public String getPassword() {
        return environment.getProperty("CAF_RABBITMQ_PASSWORD");
    }

    public Integer getPort() {
        return Integer.valueOf(environment.getProperty("CAF_RABBITMQ_PORT"));
    }

    public String getUser() {
        return environment.getProperty("CAF_RABBITMQ_USERNAME");
    }

    public Integer getMaxPriority() {
        if (environment.getProperty("CAF_RABBITMQ_MAX_PRIORITY") == null) return null;
        return Integer.valueOf(environment.getProperty("CAF_RABBITMQ_MAX_PRIORITY"));
    }
}
