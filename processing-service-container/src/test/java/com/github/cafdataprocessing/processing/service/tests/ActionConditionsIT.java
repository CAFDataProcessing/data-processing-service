/*
 * Copyright 2017-2018 Micro Focus or one of its affiliates.
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
package com.github.cafdataprocessing.processing.service.tests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsCreator;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import com.github.cafdataprocessing.processing.service.tests.utils.AdditionalPropertyComparison;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionConditionsApi;
import com.github.cafdataprocessing.processing.service.client.api.ActionsApi;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Integration tests for Action Condition API paths in the Data Processing Service.
 */
public class ActionConditionsIT {
    private static ObjectMapper mapper = new ObjectMapper();

    private static WorkflowsApi workflowsApi;
    private static ProcessingRulesApi processingRulesApi;
    private static ActionsApi actionsApi;
    private static ActionConditionsApi actionConditionsApi;

    private String projectId;

    @BeforeClass
    public static void setup() throws Exception {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        workflowsApi = new WorkflowsApi(apiClient);
        processingRulesApi = new ProcessingRulesApi(apiClient);
        actionsApi = new ActionsApi(apiClient);
        actionConditionsApi = new ActionConditionsApi(apiClient);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Before every test generate a new project ID to avoid results from previous tests affecting subsequent tests.
     */
    @BeforeMethod
    public void intializeProjectId(){
        projectId = UUID.randomUUID().toString();
    }

    @Test(description = "Creates and retrieves some action conditions.")
    public void createAndGetActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate_1 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_1 = actionConditionsApi.createActionCondition(projectId, workflowId_1,
                ruleId_1, actionId_1, conditionToCreate_1);
        compareConditions(conditionToCreate_1, createdCondition_1);

        Condition conditionToCreate_2 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_2 = actionConditionsApi.createActionCondition(projectId, workflowId_1,
                ruleId_1, actionId_1, conditionToCreate_2);
        compareConditions(conditionToCreate_2, createdCondition_2);

        //retrieve the conditions
        ExistingCondition retrievedCondition_1 = actionConditionsApi.getActionCondition(projectId, workflowId_1,
                ruleId_1, actionId_1, createdCondition_1.getId());
        compareConditions(conditionToCreate_1, retrievedCondition_1);
        Assert.assertEquals(retrievedCondition_1.getId(), createdCondition_1.getId(),
                "ID on first retrieved condition should match ID sent in request.");
        ExistingCondition retrievedCondition_2 = actionConditionsApi.getActionCondition(projectId, workflowId_1,
                ruleId_1, actionId_1, createdCondition_2.getId());
        Assert.assertEquals(retrievedCondition_2.getId(), createdCondition_2.getId(),
                "ID on second retrieved condition should match ID sent in request.");
        compareConditions(conditionToCreate_2, retrievedCondition_2);

        //verify behaviour of retrieving condition that doesn't exist
        try{
            actionConditionsApi.getActionCondition(projectId, workflowId_1, ruleId_1, actionId_1,
                    ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception to be thrown when trying to retrieve condition by ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition with ID"),
                    "Exception message should contain expected message about not finding conditon. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates an action condition with a number condition, retrieves it and updates it.")
    public void createGetAndUpdateNumberActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        NumberConditionAdditional numberConditionAdditional = ObjectsInitializer.initializeNumberConditionAdditional();
        conditionToCreate.setAdditional(numberConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        numberConditionAdditional = ObjectsInitializer.initializeNumberConditionAdditional();
        numberConditionAdditional.setOperator(NumberConditionAdditional.OperatorEnum.GT);
        numberConditionAdditional.setOrder(200);
        conditionToUpdate.setAdditional(numberConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates an action condition with a number condition that has a 64-bit number and retrieves it.")
    public void createAndGetNumberInt64ActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        Long largeValue = 1234567890123456786L;
        NumberConditionAdditional numberConditionAdditional = ObjectsInitializer.initializeNumberConditionAdditional(largeValue);
        conditionToCreate.setAdditional(numberConditionAdditional);

        createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);
    }

    @Test(description = "Creates, retrieves and updates an action condition with a regex condition.")
    public void createGetAndUpdateRegexActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        RegexConditionAdditional regexConditionAdditional = ObjectsInitializer.initializeRegexConditionAdditional();
        conditionToCreate.setAdditional(regexConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        regexConditionAdditional = ObjectsInitializer.initializeRegexConditionAdditional();
        conditionToUpdate.setAdditional(regexConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates action condition with a date condition, retrieves it and updates it.")
    public void createGetDateActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        DateConditionAdditional dateConditionAdditional = ObjectsInitializer.initializeDateConditionAdditional();
        conditionToCreate.setAdditional(dateConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        dateConditionAdditional = ObjectsInitializer.initializeDateConditionAdditional();
        conditionToUpdate.setAdditional(dateConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates action condition with a not condition and retrieves it.")
    public void createAndGetNotActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        NotConditionAdditional notConditionAdditional = ObjectsInitializer.initializeNotConditionAdditional(null);
        conditionToCreate.setAdditional(notConditionAdditional);

        createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);
    }

    @Test(description = "Creates action condition with an exists condition and retrieves it.")
    public void createAndGetExistsActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();
        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        ExistsConditionAdditional existsConditionAdditional = ObjectsInitializer.initializeExistsConditionAdditional();
        conditionToCreate.setAdditional(existsConditionAdditional);

        createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);
    }

    @Test(description = "Creates action condition with a boolean condition and retrieves it.")
    public void createAndGetBooleanActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();
        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        BooleanConditionAdditional booleanConditionAdditional = ObjectsInitializer.initializeBooleanConditionAdditional(null);

        conditionToCreate.setAdditional(booleanConditionAdditional);
        createAndRetrieveCondition(workflowId_1, ruleId_1, actionId_1, conditionToCreate);
    }

    @Test(description = "Creates some action conditions and updates one of them.")
    public void updateActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        //create some action conditions
        Condition conditionToCreate_1 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_1 = actionConditionsApi.createActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, conditionToCreate_1);

        Condition conditionToCreate_2 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_2 = actionConditionsApi.createActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, conditionToCreate_2);

        //update first condition
        Condition conditionToUpdate_1 = ObjectsInitializer.initializeCondition(null);
        actionConditionsApi.updateActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, createdCondition_1.getId(), conditionToUpdate_1);

        ExistingCondition updatedCondition = actionConditionsApi.getActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, createdCondition_1.getId());
        Assert.assertEquals(updatedCondition.getId(), createdCondition_1.getId(),
                "ID on retrieved updated condition should match ID sent in request.");
        compareConditions(conditionToUpdate_1, updatedCondition);

        try{
            actionConditionsApi.updateActionCondition(projectId, workflowId_1, ruleId_1, actionId_1,
                    ThreadLocalRandom.current().nextLong(), conditionToUpdate_1);
            Assert.fail("Expected exception to be thrown when trying to update a condition with an ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition with ID"),
                    "Expected exception message to state unable to find the condition. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates some action conditions and deletes one of them.")
    public void deleteActionCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate_1 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_1 = actionConditionsApi.createActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, conditionToCreate_1);
        Condition conditionToCreate_2 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_2 = actionConditionsApi.createActionCondition(projectId, workflowId_1, ruleId_1,
                actionId_1, conditionToCreate_2);

        actionConditionsApi.deleteActionCondition(projectId, workflowId_1, ruleId_1, actionId_1, createdCondition_1.getId());
        //verify that condition is gone
        ExistingConditions conditionsPage = actionConditionsApi.getActionConditions(projectId, workflowId_1, ruleId_1,
                actionId_1, 1, 100);
        Assert.assertEquals((int)conditionsPage.getTotalHits(), 1,
                "Expecting total hits to report only one condition on the action.");
        Assert.assertEquals(conditionsPage.getConditions().size(), 1,
                "Expecting there to only be one condition returned in the paging result.");

        ExistingCondition remainingCondition = conditionsPage.getConditions().get(0);
        Assert.assertEquals(remainingCondition.getId(), createdCondition_2.getId(),
                "Expected ID of remaining condition to be ID of second condition created.");
        compareConditions(conditionToCreate_2, remainingCondition);

        //verify that trying to delete a condition that doesn't exist fails as expected
        try{
            actionConditionsApi.deleteActionCondition(projectId, workflowId_1, ruleId_1, actionId_1, createdCondition_1.getId());
            Assert.fail("Expected exception to be thrown trying to delete a condition that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition with ID"),
                    "Expected exception message to state that condition could not be found. Message: "+e.getMessage());
        }

        //verify that trying to delete a condition passing incorrect action ID fails as expected
        ExistingAction action_2 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 200, null, null);
        long actionId_2 = action_2.getId();
        try{
            actionConditionsApi.deleteActionCondition(projectId, workflowId_1, ruleId_1, actionId_2,
                    createdCondition_2.getId());
            Assert.fail("Expected exception to be thrown trying to delete action condition when passing wrong action ID.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition with ID"),
                    "Expected exception message to state that condition could not be found. Message: "+e.getMessage());
        }

        //verify that passing incorrect rule ID causes failure
        ExistingProcessingRule rule_2 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, 200);
        try{
            actionConditionsApi.deleteActionCondition(projectId, workflowId_1, rule_2.getId(), actionId_1,
                    createdCondition_2.getId());
            Assert.fail("Expected exception to be thrown trying to delete action condition when passing wrong rule ID.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("There are no Actions on Rule with ID"),
                    "Expected exception message to state that action could not be found due to incorrect rule parent. Message: "+e.getMessage());
        }


        //verify the condition we just tried to delete can still be retrieved
        ExistingCondition retrievedCondition = actionConditionsApi.getActionCondition(projectId, workflowId_1,
                ruleId_1, actionId_1, createdCondition_2.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition_2.getId(),
                "ID on retrieved condition after failed delete should match ID on request.");
        compareConditions(createdCondition_2, retrievedCondition);
    }

    @Test(description = "Creates action conditions and pages through them.")
    public void getActionConditions() throws ApiException {
        int numberOfConditionsToCreate = 26;

        ExistingWorkflow createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = createdWorkflow.getId();
        ExistingProcessingRule createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        long ruleId = createdRule.getId();
        ExistingAction createdAction_1 = ObjectsCreator.createAction(projectId, workflowId, ruleId,
                100, null, null);
        long actionId_1 = createdAction_1.getId();

        List<ExistingCondition> createdConditions_1 = createMultipleActionConditions(numberOfConditionsToCreate,
                workflowId, ruleId, actionId_1);
        List<ExistingCondition> conditionsCopy_1 = new LinkedList<>();
        conditionsCopy_1.addAll(createdConditions_1);

        //page through the conditions
        int pageSize = 5;
        pageThroughConditions(pageSize, workflowId, ruleId, actionId_1, createdConditions_1, numberOfConditionsToCreate);

        //create another action and verify that conditions created under it are correctly pageable
        numberOfConditionsToCreate = 23;
        ExistingAction createdAction_2 = ObjectsCreator.createAction(projectId, workflowId, ruleId, 200, null, null);
        long actionId_2 = createdAction_2.getId();
        List<ExistingCondition> createdConditions_2 = createMultipleActionConditions(numberOfConditionsToCreate,
                workflowId, ruleId, actionId_2);

        pageSize = 8;
        pageThroughConditions(pageSize, workflowId, ruleId, actionId_2, createdConditions_2, numberOfConditionsToCreate);

        //verify that the original conditions are still present on the first action
        pageThroughConditions(pageSize, workflowId, ruleId, actionId_1, conditionsCopy_1, conditionsCopy_1.size());
    }

    private void pageThroughConditions(int pageSize, long workflowId, long ruleId, long actionId,
                                       List<ExistingCondition> conditionsToFind, int expectedNumberOfConditions)
            throws ApiException {
        int pageNum = 1;
        int conditionsSoFarCount = 0;
        while(true){
            ExistingConditions conditionsPage = actionConditionsApi.getActionConditions(projectId,
                    workflowId, ruleId, actionId, pageNum, pageSize);
            Assert.assertEquals((int)conditionsPage.getTotalHits(),
                    expectedNumberOfConditions, "Total hits should be the same as the expected number of conditions.");
            List<ExistingCondition> retrievedConditions = conditionsPage.getConditions();

            if(pageNum*pageSize <= expectedNumberOfConditions){
                //until we get to the page that includes the last result (or go beyond the number available) there should always be 'pageSize' number of results returned
                Assert.assertEquals(retrievedConditions.size(), pageSize, "Expecting full page of condition results.");
            }
            //remove returned conditions from list of conditions to find
            checkConditionsReturned(conditionsPage, conditionsToFind);
            //increment page num so that next call retrieves next page
            pageNum++;
            conditionsSoFarCount += retrievedConditions.size();
            if(conditionsSoFarCount > expectedNumberOfConditions){
                Assert.fail("More conditions encountered than expected.");
            }
            else if(conditionsSoFarCount == expectedNumberOfConditions){
                Assert.assertTrue(conditionsToFind.isEmpty(),
                        "After encountering the expected number of conditions there should be no more conditions that we are searching for.");
                break;
            }
        }
        //send a final get request and verify that nothing is returned.
        ExistingConditions expectedEmptyGetResult = actionConditionsApi.getActionConditions(projectId,
                workflowId, ruleId, actionId, pageNum, pageSize);

        Assert.assertEquals((int) expectedEmptyGetResult.getTotalHits(), expectedNumberOfConditions,
                "Total hits should report the expected number of conditions even on a page outside range of results.");
        Assert.assertTrue(expectedEmptyGetResult.getConditions().isEmpty(),
                "Should be no conditions returned for page request outside expected range.");
    }

    /**
     * Search the retrieved conditions passed for the occurrence of a set of conditions.
     * When a match is found for a condition it is removed from the list of conditions to find passed in.
     * @param retrievedConditions The conditions to search through.
     * @param conditionsToFind The conditions to search for.
     */
    private void checkConditionsReturned(ExistingConditions retrievedConditions,
                                         List<ExistingCondition> conditionsToFind){
        for(ExistingCondition retrievedCondition: retrievedConditions.getConditions()){
            Optional<ExistingCondition> foundCondition = conditionsToFind.stream()
                    .filter(filterCondition -> filterCondition.getId().equals(retrievedCondition.getId())).findFirst();
            if(foundCondition.isPresent()) {
                compareConditions(foundCondition.get(), retrievedCondition);
                //remove from the list of conditions for next check
                conditionsToFind.remove(foundCondition.get());
            }
            else{
                Assert.fail("An unexpected condition was returned.");
            }
        }
    }

    private List<ExistingCondition> createMultipleActionConditions(int numberOfConditionsToCreate, long workflowId,
                                                                   long ruleId, long actionId) throws ApiException {
        List<ExistingCondition> createdConditions = new LinkedList<>();
        for(int createdConditionCounter = 0; createdConditionCounter < numberOfConditionsToCreate;
            createdConditionCounter++) {
            Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
            createdConditions.add(actionConditionsApi.createActionCondition(projectId, workflowId, ruleId, actionId,
                    conditionToCreate));
        }
        return createdConditions;
    }

    /**
     * Compares a retrieved condition against an expected version of it.
     * @param expectedCondition The condition expected.
     * @param retrievedCondition The condition that was returned.
     */
    private static void compareConditions(Condition expectedCondition, ExistingCondition retrievedCondition){
        Assert.assertEquals(retrievedCondition.getName(), expectedCondition.getName(),
                "Name on rule condition should be as expected.");
        ConditionCommon createdAdditional = mapper.convertValue(retrievedCondition.getAdditional(), ConditionCommon.class);
        ConditionCommon expectedAdditional = mapper.convertValue(expectedCondition.getAdditional(), ConditionCommon.class);
        AdditionalPropertyComparison.compareAdditional(expectedAdditional.getType(), createdAdditional.getType(),
                expectedCondition.getAdditional(), retrievedCondition.getAdditional());
    }

    private long createAndRetrieveCondition(long workflowId, long ruleId, long actionId, Condition conditionToCreate) throws ApiException {
        ExistingCondition createdCondition = actionConditionsApi.createActionCondition(projectId, workflowId,
                ruleId, actionId, conditionToCreate);
        compareConditions(conditionToCreate, createdCondition);

        ExistingCondition retrievedCondition = actionConditionsApi.getActionCondition(projectId, workflowId, ruleId,
                actionId, createdCondition.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition.getId(),
                "ID of retrieved condition should match that requested.");
        compareConditions(createdCondition, retrievedCondition);
        return retrievedCondition.getId();
    }

    private void updateAndRetrieveCondition(long workflowId, long ruleId, long actionId, long conditionId,
                                            Condition conditionToUpdate) throws ApiException {
        actionConditionsApi.updateActionCondition(projectId, workflowId, ruleId, actionId, conditionId,
                conditionToUpdate);

        ExistingCondition retrievedCondition = actionConditionsApi.getActionCondition(projectId, workflowId, ruleId,
                actionId, conditionId);
        Assert.assertEquals((long) retrievedCondition.getId(), conditionId,
                "ID of retrieved condition after update should match that requested.");
        compareConditions(conditionToUpdate, retrievedCondition);
    }
}
