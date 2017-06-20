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
package com.github.cafdataprocessing.utilities.familytasksubmitter;

import com.github.cafdataprocessing.utilities.familytasksubmitter.taskmessage.RabbitMessageDispatcher;
import com.github.cafdataprocessing.utilities.familytasksubmitter.document.DocumentExtractor;
import com.github.cafdataprocessing.utilities.familytasksubmitter.document.HierarchyBuilder;
import com.github.cafdataprocessing.utilities.familytasksubmitter.elasticsearch.ElasticQuery;
import com.github.cafdataprocessing.utilities.familytasksubmitter.taskmessage.Message;
import com.google.common.collect.Multimap;
import com.hp.autonomy.policyworker.shared.Document;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Main entry point for family task submitter application, reading in arguments and performing application function
 */
public final class Main
{

    public static void main(String[] args) throws ConfigurationException, IOException, CodecException, TimeoutException
    {
        new Main().run();
        System.exit(0);
    }

    public void run() throws CodecException, IOException, ConfigurationException, TimeoutException
    {
        final String familyReference = getEnvironmentValue(FamilyTaskSubmitterConstants.FAMILY_REFERENCE); 
        final ElasticQuery esQuery = new ElasticQuery(familyReference);
        final Map<String, Object> family = esQuery.createFamily(familyReference);
        final DocumentExtractor docBuilder = new DocumentExtractor(family);
        final Map<String, Multimap<String, String>> documents = docBuilder.convert();
        final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
        final Document rootDocument = hierarchyBuilder.convert(documents, familyReference);
        final Message message = new Message(rootDocument, getEnvironmentValue(FamilyTaskSubmitterConstants.OUTPUT_QUEUE_NAME));
        final RabbitMessageDispatcher messageDispatcher = new RabbitMessageDispatcher();
        messageDispatcher.configure(getEnvironmentValue(FamilyTaskSubmitterConstants.OUTPUT_QUEUE_NAME));
        messageDispatcher.sendMessage(message.createTaskMessage());
    }

    private static String getEnvironmentValue(final String name)
    {
        return System.getProperty(name, System.getenv(name) != null ? System.getenv(name) : "");
    }
}
