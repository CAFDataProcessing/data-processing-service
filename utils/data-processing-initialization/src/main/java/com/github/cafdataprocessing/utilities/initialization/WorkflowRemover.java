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
package com.github.cafdataprocessing.utilities.initialization;

import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.ExistingProcessingRule;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflows;
import com.github.cafdataprocessing.processing.service.client.model.ProcessingRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes processing workflows (including its components) based on project ID and
 * matching names in preparation for creating new workflows.
 */
public class WorkflowRemover {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRemover.class);

    /**
     * Removes any existing processing workflows (including processing rules etc under the workflow) under the specified projectId
     * that match the name provided.
     * @param apisProvider Provides access to processing service APIs so retrieval and delete requests may be sent via
     *                     the appropriate API.
     * @param projectId ProjectId that checked workflows should be under.
     * @param workflowNameToRemove If any workflows have a name that matches this value they will be removed.
     * @throws ApiException If an error occurs contacting the processing API.
     */
    public static void removeMatchingWorkflows(final ProcessingApisProvider apisProvider, final String projectId,
                                               final String workflowNameToRemove) throws ApiException {
        LOGGER.info("Checking for existing processing workflows that should be removed using name: "
                +workflowNameToRemove);
        if(workflowNameToRemove==null){
            LOGGER.warn("Workflow name to use in checking existing processing workflows to remove cannot be null. " +
                    "Workflows will not be checked.");
            return;
        }
        final WorkflowsApi workflowsApi = apisProvider.getWorkflowsApi();
        List<ExistingWorkflow> existingWorkflows = retrieveExistingWorkflows(workflowsApi, projectId);

        if(existingWorkflows.isEmpty()){
            LOGGER.info("There are no existing processing workflows to remove.");
            return;
        }
        final ProcessingRulesApi processingRulesApi = apisProvider.getProcessingRulesApi();
        for(ExistingWorkflow existingWorkflow: existingWorkflows){
            final Long existingWorkflowId = existingWorkflow.getId();
            if(existingWorkflow.getName().equals(workflowNameToRemove)){
                LOGGER.debug("Existing processing workflow matches name: "+workflowNameToRemove+", has ID: "
                        +existingWorkflowId+
                        ". Workflow will be removed.");
                List<ExistingProcessingRule> processingRulesToRemove = new ArrayList<>();
                int pageNum = 1;
                final int pageSize = 100;
                while(true){
                    ProcessingRules retrieveProcessingRulesResult =
                            processingRulesApi.getRules(projectId, existingWorkflowId, pageNum, pageSize);
                    processingRulesToRemove.addAll(retrieveProcessingRulesResult.getRules());
                    if(retrieveProcessingRulesResult.getTotalHits() <= pageNum*pageSize){
                        break;
                    }
                    pageNum++;
                }
                removeProcessingRules(processingRulesApi, projectId, existingWorkflowId, processingRulesToRemove);
                LOGGER.debug("Removed all processing rules for workflow: "+existingWorkflowId);
                workflowsApi.deleteWorkflow(projectId, existingWorkflowId);
                LOGGER.debug("Removed processing workflow: "+existingWorkflowId);
            }
            LOGGER.info("Removed any existing processing workflows with name: "+workflowNameToRemove);
        }
    }

    private static void removeProcessingRules(final ProcessingRulesApi processingRulesApi, final String projectId,
                                              final Long workflowId,
                                              final List<ExistingProcessingRule> processingRulesToRemove) throws ApiException {
        for(ExistingProcessingRule processingRuleToRemove: processingRulesToRemove){
            final Long ruleToRemoveId = processingRuleToRemove.getId();
            LOGGER.debug("Removing processing rule with ID: "+ ruleToRemoveId +
                    " under workflow with ID: "+workflowId);
            processingRulesApi.deleteRule(projectId, workflowId, ruleToRemoveId);
            LOGGER.debug("Removed processing rule with ID: "+ruleToRemoveId);
        }
    }

    private static List<ExistingWorkflow> retrieveExistingWorkflows(final WorkflowsApi workflowsApi, final String projectId)
            throws ApiException {
        List<ExistingWorkflow> existingWorkflows = new ArrayList<>();
        {
            int pageNum = 1;
            final int pageSize = 100;
            LOGGER.debug("Retrieving existing processing workflows to check their names");
            while(true){
                final ExistingWorkflows retrieveWorkflowResult = workflowsApi.getWorkflows(projectId, pageNum, pageSize);
                existingWorkflows.addAll(retrieveWorkflowResult.getWorkflows());
                if(retrieveWorkflowResult.getTotalHits() <= pageNum*pageSize){
                    break;
                }
                pageNum++;
            }
            LOGGER.debug("Retrieved all existing processing workflows.");
        }
        return existingWorkflows;
    }
}
