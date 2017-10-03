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

import com.github.cafdataprocessing.classification.service.client.ApiClient;
import com.github.cafdataprocessing.classification.service.client.ApiException;
import com.github.cafdataprocessing.classification.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.classification.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.classification.service.client.model.ExistingWorkflows;
import com.github.cafdataprocessing.classification.service.creation.created.CreatedWorkflow;
import com.github.cafdataprocessing.classification.service.creation.created.CreationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resolves the names of classification workflows used in actions to their IDs
 */
public class ClassificationWorkflowNameResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationWorkflowNameResolver.class);

    private final Map<String, Long> namesToIds = new LinkedHashMap<>();
    private final WorkflowsApi workflowsApi;

    /**
     * Create new instance of ClassificationWorkflowNameResolver using specified ApiClient.
     * @param apiClient Used to create a Workflow API to contact for resolution. If null passed then name resolution
     *                  via external API calls will not be possible.
     */
    public ClassificationWorkflowNameResolver(ApiClient apiClient){
        if(apiClient!=null) {
            this.workflowsApi = new WorkflowsApi(apiClient);
        }
        else {
            this.workflowsApi = null;
        }
    }

    /**
     * Populate map of classification workflow names to IDs using the Creation Result.
     * @param creationResult Classification Workflow Creation Result to populate from.
     */
    public void populateFromCreationResult(CreationResult creationResult){
        if(creationResult==null) {
            return;
        }
        CreatedWorkflow createdWorkflow = creationResult.getWorkflow();
        if (createdWorkflow == null) {
            return;
        }
        namesToIds.put(createdWorkflow.getName(), createdWorkflow.getId());
    }

    /**
     * Populates the known mapping of workflow names to workflow IDs by querying the classification API for all existing workflows.
     * @param projectId Project ID to use in workflow requests.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public void populateWorkflowsFromApiCall(String projectId) throws ApiException {
        if(workflowsApi==null){
            LOGGER.error("Classification API is not configured for usage, unable to retrieve expressions.");
            return;
        }

        int pageNum = 1;
        int pageSize = 100;
        int retrievedHitsSize;
        do {
            ExistingWorkflows workflows = workflowsApi.getWorkflows(projectId, pageNum, pageSize);
            pageNum++;
            retrievedHitsSize = workflows.getWorkflows().size();
            for(ExistingWorkflow workflow : workflows.getWorkflows()){
                if(namesToIds.containsKey(workflow.getName())){
                    continue;
                }
                namesToIds.put(workflow.getName(), workflow.getId());
            }
        }
        while(retrievedHitsSize >= pageSize);
    }

    /**
     * Resolves provided classification workflow name to matching ID.
     * @param workflowName Classification workflow name to get ID for.
     * @return Corresponding ID for provided name.
     */
    public Long resolveFromWorkflowName(String workflowName){
        return namesToIds.get(workflowName);
    }
}
