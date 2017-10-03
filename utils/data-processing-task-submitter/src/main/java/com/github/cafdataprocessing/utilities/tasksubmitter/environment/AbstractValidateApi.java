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
package com.github.cafdataprocessing.utilities.tasksubmitter.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common methods for use when validating connection to external API's.
 */
public abstract class AbstractValidateApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateBoilerplateProperties.class);
    private static final int apiCallDelayMs = 5000;

    /**
     * Delays thread progression.
     * @param interruptedMessage Message to log in the event of an interrupted exception being thrown and caught.
     */
    protected static void delayCall(String interruptedMessage){
        try {
            Thread.sleep(apiCallDelayMs);
        } catch (InterruptedException e) {
            LOGGER.warn(interruptedMessage);
        }
    }
}
