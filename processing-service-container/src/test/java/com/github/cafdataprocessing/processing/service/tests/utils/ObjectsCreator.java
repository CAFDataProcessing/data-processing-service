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
package com.github.cafdataprocessing.processing.service.tests.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionTypesApi;
import com.github.cafdataprocessing.processing.service.client.api.ActionsApi;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.*;

/**
 * Creates processing service API types, calling the running API create methods and returning the response object.
 */
public class ObjectsCreator {
    private static ObjectMapper mapper = new ObjectMapper();

    private static WorkflowsApi workflowsApi;
    private static ProcessingRulesApi processingRulesApi;
    private static ActionTypesApi actionTypesApi;
    private static ActionsApi actionsApi;

    static {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        workflowsApi = new WorkflowsApi(apiClient);
        processingRulesApi = new ProcessingRulesApi(apiClient);
        actionTypesApi = new ActionTypesApi(apiClient);
        actionsApi = new ActionsApi(apiClient);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Creates a workflow.
     * @param projectId projectId to create workflow under.
     * @return Created Workflow.
     */
    public static ExistingWorkflow createWorkflow(String projectId) throws ApiException {
        BaseWorkflow workflow = ObjectsInitializer.initializeWorkflow();
        return workflowsApi.createWorkflow(projectId, workflow);
    }

    /**
     * Creates a processing rule.
     * @param projectId projectId to create processing rule under.
     * @param workflowId ID of the workflow to create processing rule under.
     * @param priority The priority to set on the processing rule. If null then the API determines priority.
     * @return Created processing rule.
     * @throws ApiException
     */
    public static ExistingProcessingRule createProcessingRule(String projectId, long workflowId,
                                                              Integer priority) throws ApiException {
        BaseProcessingRule ruleToCreate = ObjectsInitializer.initializeProcessingRule(priority);
        return processingRulesApi.createRule(projectId, workflowId, ruleToCreate);
    }

    /**
     * Creates an action type.
     * @param projectId projectId to create action type under.
     * @return Created action type.
     * @throws ApiException
     */
    public static ExistingActionType createActionType(String projectId) throws ApiException {
        ActionType actionTypeToCreate_1 = ObjectsInitializer.initializeActionType(null);
        return actionTypesApi.createActionType(projectId,
                actionTypeToCreate_1);
    }

    /**
     * Creates an action.
     * @param projectId projectId to create action type under.
     * @param workflowId ID of the workflow to create action under.
     * @param ruleId ID of the rule to create action under.
     * @param order order to set on the action.
     * @param typeId typeId to use for the action. If null then a new type will be created and used on the action.
     * @param settings settings to use for the action. If typeId is null and this is null they will be generated for the action.
     * @return The created action.
     * @throws ApiException
     */
    public static ExistingAction createAction(String projectId, long workflowId, long ruleId,
                                              int order, Long typeId, Object settings) throws ApiException {
        long typeIdToUse;
        Object settingsToUse = null;
        if(typeId==null){
            ExistingActionType type = ObjectsCreator.createActionType(projectId);
            ExampleActionTypeDefinition typeDefinition = mapper.convertValue(type.getDefinition(),
                    ExampleActionTypeDefinition.class);
            typeIdToUse = type.getId();
            if(settings==null){
                settingsToUse = typeDefinition.generateSettingsForType();
            }
        }
        else{
            typeIdToUse = typeId;
        }
        if(settings!=null){
            settingsToUse = settings;
        }
        Action actionToCreate = ObjectsInitializer.initializeAction(order, typeIdToUse, settingsToUse);
        return actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate);
    }
}
