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
package com.github.cafdataprocessing.utilities.initialization;

import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.*;
import com.github.cafdataprocessing.processing.service.client.model.Action;
import com.github.cafdataprocessing.processing.service.client.model.ExistingAction;
import com.github.cafdataprocessing.processing.service.client.model.ExistingProcessingRule;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.utilities.initialization.boilerplate.BoilerplateNameResolver;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ActionJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ProcessingRuleJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.WorkflowJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions.ConditionJson;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Responsible for creation of a data processing workflow using defined base data.
 */
public class WorkflowInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowInitializer.class);

    private final WorkflowsApi workflowsApi;
    private final ProcessingRulesApi processingRulesApi;
    private final ActionsApi actionsApi;
    private final ActionTypesApi actionTypesApi;
    private final ActionConditionsApi actionConditionsApi;
    private final ProcessingRulesConditionsApi rulesConditionsApi;

    private final ProcessingApisProvider apisProvider;

    private ActionTypeNameResolver actionTypeNameResolver;
    private final BoilerplateNameResolver boilerplateNameResolver;
    private final ClassificationWorkflowNameResolver classificationWorkflowNameResolver;

    /**
     * Create instance of WorkflowInitializer with provided parameters.
     * @param processingApiUrl URL of processing API to contact during initialization.
     * @param boilerplateNameResolver for use in resolving expression/tag names to IDs in workflow.
     * @param actionTypeNameResolver for use in resolving action type names to IDs in workflow.
     * @param classificationWorkflowNameResolver for use in resolving classification workflow names to IDs in processing
     *                                           workflow.
     */
    public WorkflowInitializer(String processingApiUrl, BoilerplateNameResolver boilerplateNameResolver,
                               ActionTypeNameResolver actionTypeNameResolver,
                               ClassificationWorkflowNameResolver classificationWorkflowNameResolver){
        final ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(processingApiUrl);
        apisProvider = new ProcessingApisProvider(apiClient);
        workflowsApi = apisProvider.getWorkflowsApi();
        processingRulesApi = apisProvider.getProcessingRulesApi();
        actionsApi = apisProvider.getActionsApi();
        actionTypesApi = apisProvider.getActionTypesApi();
        actionConditionsApi = apisProvider.getActionConditionsApi();
        rulesConditionsApi = apisProvider.getRulesConditionsApi();

        this.actionTypeNameResolver = actionTypeNameResolver;
        this.boilerplateNameResolver = boilerplateNameResolver;
        this.classificationWorkflowNameResolver = classificationWorkflowNameResolver;
    }

    public static WorkflowInitializer createWorkflowInitializer(String processingApiUrl, String boilerplateApiUrl, String classificationApiUrl)
    {
        com.hpe.caf.boilerplate.webcaller.ApiClient boilerplateClient = new com.hpe.caf.boilerplate.webcaller.ApiClient();
        boilerplateClient.setBasePath(boilerplateApiUrl);
        BoilerplateNameResolver boilerplateNameResolver = new BoilerplateNameResolver(boilerplateClient);

        com.github.cafdataprocessing.classification.service.client.ApiClient classificationClient = new com.github.cafdataprocessing.classification.service.client.ApiClient();
        classificationClient.setBasePath(classificationApiUrl);

        ClassificationWorkflowNameResolver classificationWorkflowNameResolver = new ClassificationWorkflowNameResolver(classificationClient);

        return new WorkflowInitializer(processingApiUrl, boilerplateNameResolver, new ActionTypeNameResolver(), classificationWorkflowNameResolver);
    }

    public static WorkflowInitializer createWorkflowOnlyInitializer(String processingApiUrl)
    {
        return new WorkflowInitializer(processingApiUrl,
                                       new BoilerplateNameResolver(null),
                                       new ActionTypeNameResolver(),
                                       new ClassificationWorkflowNameResolver(null));
    }

    private void retrieveActionTypeInformation(String projectId) throws ApiException {
        LOGGER.debug("Retrieving action types information for action creation.");
        actionTypeNameResolver.populateActionTypesFromApi(actionTypesApi, projectId);
        LOGGER.debug("Retrieved action types information for action creation.");
    }

    /**
     * Creates a processing workflow derived from the information in the input file provided.
     * @param inputFileName File containing workflow in JSON format that should be created.
     * @param projectId ProjectId to create workflow under.
     * @return ID of created workflow.
     * @throws IOException If unable to read the workflow input file.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public long initializeWorkflowBaseData(String inputFileName, String projectId) throws IOException, ApiException
    {
        return initializeWorkflowBaseData(inputFileName, null, projectId);
    }

    /**
     * Creates a processing workflow derived from the information in the input file provided.
     * @param inputFileName File containing workflow in JSON format that should be created.
     * @param overlayFile Optional file containing extensions / overrides to merge with the base workflow specified in
     *                    inputFileName. Pass null if you do not want any overrides to be applied.
     * @param projectId ProjectId to create workflow under.
     * @return ID of created workflow.
     * @throws IOException If unable to read the workflow input file.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public long initializeWorkflowBaseData(String inputFileName, String overlayFile, String projectId) throws IOException, ApiException {
        //retrieve action types that this project ID has available

        //read in the json representation of the workflow
        final WorkflowJson workflowToCreate = WorkflowJson.readInputFile(inputFileName);
        final WorkflowJson overlayWorkflow = Strings.isNullOrEmpty(overlayFile) ? null : WorkflowJson.readInputFile(overlayFile);

        //create the workflow and return ID of created workflow
        return createWorkflow(workflowToCreate, overlayWorkflow, projectId);
    }

    /**
     * Creates a processing workflow derived from the information in the input file provided.
     * @param workflowBaseDataStream Input stream containing workflow in JSON format that should be created.
     * @param workflowOverlayStream Optional input stream containing extensions / overrides to merge with the base
     *                              workflow specified in inputFileName. Pass null if you do not want any overrides
     *                              to be applied.
     * @param projectId ProjectId to create workflow under.
     * @return ID of created workflow.
     * @throws IOException If unable to read the workflow input file.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public long initializeWorkflowBaseData(InputStream workflowBaseDataStream, InputStream workflowOverlayStream, String projectId) throws IOException, ApiException {

        //read in the json representation of the workflow
        final WorkflowJson workflowToCreate = WorkflowJson.readInputStream(workflowBaseDataStream);
        final WorkflowJson overlayWorkflow = workflowOverlayStream == null ? null : WorkflowJson.readInputStream(workflowOverlayStream);

        //create the workflow and return ID of created workflow
        return createWorkflow(workflowToCreate, overlayWorkflow, projectId);
    }

    /**
     * Creates a processing workflow derived from the information in the {@code initializationParams} object. Optional extensions or
     * overrides can be provided in the {@code workflowOverlay} parameter of the {@code initializationParams} parameter.
     * See {@link WorkflowCombiner} for more information on merging of two {@link WorkflowJson} objects.
     * @param initializationParams Defines properties to use in workflow creation.
     * @return Newly created workflow id.
     * @throws IOException If unable to read the workflow.
     * @throws ApiException in case of a failure to create a workflow.
     */
    public long initializeWorkflowBaseData(WorkflowInitializationParams initializationParams) throws IOException, ApiException {
        return createWorkflow(initializationParams.getWorkflowBaseDataJson(),
                initializationParams.getWorkflowOverlayJson(),
                initializationParams.getProjectId(),
                initializationParams.getOverwriteExisting());
    }

    /**
     * Creates a processing workflow derived from the information in the {@code workflow} object. Optional extensions or
     * overrides can be provided in the {@code workflowOverlay} parameter.
     * See {@link WorkflowCombiner} for more information on merging of two {@link WorkflowJson} objects.
     * This will remove any existing workflows with the same workflow name as the definition from the {@code workflow} parameter for the
     * specified {@code projectId}.
     * @param workflow An object describing workflow to be created.
     * @param workflowOverlay An optional object describing extensions or overrides to the {@code workflow} parameter.
     * @param projectId A project id to create workflow under.
     * @return Newly created workflow id.
     * @throws ApiException in case of a failure to create a workflow.
     */
    public long createWorkflow(WorkflowJson workflow, WorkflowJson workflowOverlay, String projectId) throws ApiException {
        return createWorkflow(workflow, workflowOverlay, projectId, true);
    }

    /**
     * Creates a processing workflow derived from the information in the {@code workflow} object. Optional extensions or
     * overrides can be provided in the {@code workflowOverlay} parameter.
     * See {@link WorkflowCombiner} for more information on merging of two {@link WorkflowJson} objects.
     * @param workflow An object describing workflow to be created.
     * @param workflowOverlay An optional object describing extensions or overrides to the {@code workflow} parameter.
     * @param projectId A project id to create workflow under.
     * @param overwriteExisting Whether existing workflows under this {@code projectId} with the same name as the provided workflow should be removed.
     * @return Newly created workflow id.
     * @throws ApiException in case of a failure to create a workflow.
     */
    public long createWorkflow(WorkflowJson workflow, WorkflowJson workflowOverlay, String projectId, boolean overwriteExisting) throws ApiException {

        //retrieve action types that this project ID has available
        retrieveActionTypeInformation(projectId);

        WorkflowCombiner.combineWorkflows(workflow, workflowOverlay);

        if(overwriteExisting){
            WorkflowRemover.removeMatchingWorkflows(apisProvider, projectId, workflow.name);
        }

        ExistingWorkflow createdWorkflow = workflowsApi.createWorkflow(projectId, workflow.toApiBaseWorkflow());
        long createdWorkflowId = createdWorkflow.getId();
        for(ProcessingRuleJson ruleToCreate: workflow.processingRules) {
            createProcessingRule(ruleToCreate, createdWorkflowId, projectId);
        }
        return createdWorkflowId;
    }

    private void createProcessingRule(ProcessingRuleJson processingRule, long workflowId, String projectId) throws ApiException {
        ExistingProcessingRule createdRule = processingRulesApi.createRule(projectId, workflowId, processingRule.toApiBaseProcessingRule());
        long createdRuleId = createdRule.getId();
        for(ActionJson actionToCreate: processingRule.actions){
            createAction(actionToCreate, workflowId, createdRuleId, projectId);
        }
        for(ConditionJson conditionToCreate: processingRule.ruleConditions){
            createRuleCondition(conditionToCreate, workflowId, createdRuleId, projectId);
        }
    }

    private void createAction(ActionJson actionJson, long workflowId, long ruleId, String projectId) throws ApiException {
        Action actionToCreate = actionJson.toApiAction(actionTypeNameResolver.getActionTypesNamesToIds());
        //if this action has boilerplate expression/tag names instead of IDs, check if matches can be found in the list of known boilerplate expressions
        replaceBoilerplateNamesIfRequired(actionToCreate);
        //if this action has classification workflow names instead of IDs, check if matches can be found in the list of known classification workflows
        replaceClassificationWorkflowNamesIfRequired(actionToCreate, projectId);
        ExistingAction createdAction;
        try {
            createdAction = actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate);
        }
        catch(ApiException e){
            throw new RuntimeException("Failure trying to create action with name: "+actionToCreate.getName(), e);
        }
        long createdActionId = createdAction.getId();
        for(ConditionJson conditionToCreate: actionJson.actionConditions){
            createActionCondition(conditionToCreate, workflowId, ruleId, createdActionId, projectId);
        }
    }

    private void createActionCondition(ConditionJson conditionJson, long workflowId, long ruleId, long actionId, String projectId) throws ApiException {
        actionConditionsApi.createActionCondition(projectId, workflowId, ruleId, actionId, conditionJson.toApiCondition());
    }

    private void createRuleCondition(ConditionJson conditionJson, long workflowId, long ruleId, String projectId) throws ApiException {
        rulesConditionsApi.createRuleCondition(projectId, workflowId, ruleId, conditionJson.toApiCondition());
    }

    private void replaceBoilerplateNamesIfRequired(Action action){
        //determine if this is a boilerplate action
        Long actionTypeId = action.getTypeId();
        String actionTypeInternalName = actionTypeNameResolver.getActionTypesIdsToNames().get(actionTypeId);
        if(!actionTypeInternalName.equals("BoilerplatePolicyType")){
            //not a boilerplate action, no change required
            return;
        }
        //performing this cast with the knowledge that ActionJson settings are this type
        LinkedHashMap<String, Object> actionSettings  = (LinkedHashMap<String, Object>) action.getSettings();
        String expressionIdsKey = "expressionIds";
        Object expressionIdsObj = actionSettings.get(expressionIdsKey);
        if(expressionIdsObj!=null) {
            List<Object> updatedExpressionIds = boilerplateNameResolver.resolveExpressionIds((ArrayList) expressionIdsObj);
            actionSettings.put(expressionIdsKey, updatedExpressionIds);
        }

        String tagIdKey = "tagId";
        Object tagIdObj = actionSettings.get(tagIdKey);
        if(tagIdObj!=null) {
            Object updatedTagId = boilerplateNameResolver.resolveTagId(tagIdObj);
            actionSettings.put(tagIdKey, updatedTagId);
        }
    }

    private void replaceClassificationWorkflowNamesIfRequired(Action action, String projectId) {
        //determine if this is a workflow action
        Long actionTypeId = action.getTypeId();
        String actionTypeInternalName = actionTypeNameResolver.getActionTypesIdsToNames().get(actionTypeId);
        if(!actionTypeInternalName.equals("ElasticSearchClassificationPolicyType")
                &&
                !actionTypeInternalName.equals("ExternalClassificationPolicyType")){
            //not an external classification action, no change required
            return;
        }
        //performing this cast with the knowledge that ActionJson settings are this type
        LinkedHashMap<String, Object> actionSettings  = (LinkedHashMap<String, Object>) action.getSettings();
        String workflowIdKey = "workflowId";
        Object workflowIdObj = actionSettings.get(workflowIdKey);
        if(workflowIdObj==null) {
            return;
        }
        Object updatedWorkflowId = classificationWorkflowNameResolver.resolveFromWorkflowName((String)workflowIdObj);
        if(updatedWorkflowId==null){
            //unable to find a match, populate the names by contacting the classification API and loading workflows then
            //try again
            LOGGER.debug("Unable to find match for classification workflow name on action. "+
                    "Retrieving classification workflows information from API.");
            try {
                classificationWorkflowNameResolver.populateWorkflowsFromApiCall(projectId);
            } catch (com.github.cafdataprocessing.classification.service.client.ApiException e) {
                LOGGER.warn("Unable to retrieve classification workflows information from classification API while creating action.",
                        e);
                return;
            }
            //try to retrieve name again after update
            updatedWorkflowId = classificationWorkflowNameResolver.resolveFromWorkflowName((String)workflowIdObj);
            if(updatedWorkflowId==null){
                LOGGER.debug("Unable to resolve the name of classification workflow used on action to an ID. Action name: "+
                action.getName()+", if the value specified is a valid ID then the action is still valid.");
                return;
            }
        }
        actionSettings.put(workflowIdKey, updatedWorkflowId);

    }
}
