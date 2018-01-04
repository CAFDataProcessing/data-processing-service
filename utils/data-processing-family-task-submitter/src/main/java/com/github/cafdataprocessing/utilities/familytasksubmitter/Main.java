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
package com.github.cafdataprocessing.utilities.familytasksubmitter;

import com.github.cafdataprocessing.utilities.familytasksubmitter.document.DocumentExtractor;
import com.github.cafdataprocessing.utilities.familytasksubmitter.document.DocumentWorkerDocumentTaskMessageFactory;
import com.github.cafdataprocessing.utilities.familytasksubmitter.document.HierarchyBuilder;
import com.github.cafdataprocessing.utilities.familytasksubmitter.elasticsearch.ElasticQuery;
import com.github.cafdataprocessing.utilities.familytasksubmitter.taskmessage.RabbitMessageDispatcher;
import com.github.cafdataprocessing.workflow.constants.WorkflowWorkerConstants;
import com.google.common.collect.Multimap;
import com.hpe.caf.api.Codec;
import com.hpe.caf.api.CodecException;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.util.ServiceFunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Main entry point for family task submitter application, reading in arguments and performing application function
 */
public final class Main
{
    private static final Codec CODEC = ServiceFunctions.loadService(Codec.class);

    public static void main(String[] args) throws ConfigurationException, IOException, CodecException, TimeoutException
    {
        new Main().run();
        System.exit(0);
    }

    public void run() throws CodecException, IOException, ConfigurationException, TimeoutException
    {
        final String familyReference = FamilyTaskSubmitterProperties.getFamilyReference();
        final DocumentWorkerDocument rootDocument = buildDocument(familyReference);
        final String outputPartialReference = FamilyTaskSubmitterProperties.getOutputPartialReference();
        final String projectId = FamilyTaskSubmitterProperties.getProjectId();
        final long workflowId = FamilyTaskSubmitterProperties.getWorkflowId();
        submitTask(rootDocument, outputPartialReference, projectId, workflowId);
    }

    /**
     * Builds a family consisting of a root family document and its sub-documents based on provided family reference.
     * @param familyReference Family Reference field value that represents the family and can be used to build document
     *                        family.
     * @return Constructed family of documents.
     */
    private static DocumentWorkerDocument buildDocument(String familyReference) {
        final ElasticQuery esQuery;
        try {
            esQuery = new ElasticQuery(familyReference);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failure to configure Elasticsearch query with family reference: "+familyReference,
                    e);
        }
        final Map<String, Object> family = esQuery.createFamily(familyReference);
        final DocumentExtractor docBuilder = new DocumentExtractor(family);
        final Map<String, Multimap<String, String>> documentsMetadataMap = docBuilder.convert();
        final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
        return hierarchyBuilder.convert(documentsMetadataMap, familyReference);
    }



    private static void submitTask(final DocumentWorkerDocument documentToSend,
                                   final String outputPartialReference,
                                   final String projectId,
                                   final long workflowId) throws IOException, TimeoutException, CodecException {
        String queueToSendTo = FamilyTaskSubmitterProperties.getOutputQueueName();
        final RabbitMessageDispatcher messageDispatcher = new RabbitMessageDispatcher();
        messageDispatcher.configure(queueToSendTo);
        Map<String, String> customData = new HashMap<>();
        customData.put(WorkflowWorkerConstants.CustomData.OUTPUT_PARTIAL_REFERENCE, outputPartialReference);
        customData.put(WorkflowWorkerConstants.CustomData.PROJECT_ID, projectId);
        customData.put(WorkflowWorkerConstants.CustomData.WORKFLOW_ID, Long.toString(workflowId));

        messageDispatcher.sendMessage(DocumentWorkerDocumentTaskMessageFactory.createTaskMessage(documentToSend,
                customData, queueToSendTo, CODEC));
    }
}
