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
package com.github.cafdataprocessing.processing.service.tests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsCreator;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import com.github.cafdataprocessing.processing.service.tests.utils.AdditionalPropertyComparison;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesConditionsApi;
import com.github.cafdataprocessing.processing.service.client.model.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Integration tests for Processing Rule Condition API paths in the Data Processing Service.
 */
public class ProcessingRuleConditionsIT {
    private static ObjectMapper mapper = new ObjectMapper();

    private static ProcessingRulesConditionsApi ruleConditionsApi;
    private String projectId;

    @BeforeClass
    public static void setup() throws Exception {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        ruleConditionsApi = new ProcessingRulesConditionsApi(apiClient);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Before every test generate a new project ID to avoid results from previous tests affecting subsequent tests.
     */
    @BeforeMethod
    public void intializeProjectId(){
        projectId = UUID.randomUUID().toString();
    }

    @Test(description = "Creates some rule conditions and retrieves them.")
    public void createAndGetRuleCondition() throws ApiException {
        ExistingWorkflow createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = createdWorkflow.getId();
        ExistingProcessingRule createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        long ruleId = createdRule.getId();
        Condition condition_1 = ObjectsInitializer.initializeCondition(null);

        ExistingCondition createdCondition_1 = ruleConditionsApi.createRuleCondition(projectId, workflowId, ruleId,
                condition_1);
        compareConditions(condition_1, createdCondition_1);

        //create another rule condition under this workflow and processing rule
        Condition condition_2 = ObjectsInitializer.initializeCondition(null);
        BooleanConditionAdditional booleanConditionAdditional = ObjectsInitializer.initializeBooleanConditionAdditional(null);
        condition_2.setAdditional(booleanConditionAdditional);

        ExistingCondition createdCondition_2 = ruleConditionsApi.createRuleCondition(projectId,
                workflowId, ruleId, condition_2);
        compareConditions(condition_2, createdCondition_2);

        //retrieve the conditions
        ExistingCondition retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId,
                ruleId, createdCondition_1.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition_1.getId(),
                "ID of retrieved condition should match ID requested");
        compareConditions(createdCondition_1, retrievedCondition);

        retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId,
                ruleId, createdCondition_2.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition_2.getId(),
                "ID of retrieved condition should match ID requested");
        compareConditions(createdCondition_2, retrievedCondition);
    }

    @Test(description = "Creates a rule condition and then updates it.")
    public void updateRuleCondition() throws ApiException {
        ExistingWorkflow createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = createdWorkflow.getId();
        ExistingProcessingRule createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        long ruleId = createdRule.getId();
        Condition condition_1 = ObjectsInitializer.initializeCondition(null);

        ExistingCondition createdCondition_1 = ruleConditionsApi.createRuleCondition(projectId, workflowId,
                ruleId, condition_1);
        compareConditions(condition_1, createdCondition_1);

        ExistingCondition retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId,
                ruleId, createdCondition_1.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition_1.getId(),
                "ID of retrieved condition should match ID requested");
        compareConditions(createdCondition_1, retrievedCondition);

        //update the condition
        Condition updatedCondition = ObjectsInitializer.initializeCondition(null);
        BooleanConditionAdditional booleanConditionAdditional = ObjectsInitializer.initializeBooleanConditionAdditional(null);
        updatedCondition.setAdditional(booleanConditionAdditional);

        ruleConditionsApi.updateRuleCondition(projectId, workflowId, ruleId, createdCondition_1.getId(),
                updatedCondition);

        ExistingCondition retrievedUpdatedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId,
                ruleId, createdCondition_1.getId());
        Assert.assertEquals(retrievedUpdatedCondition.getId(), retrievedUpdatedCondition.getId(),
                "ID of updated retrieved condition should match ID requested");
        compareConditions(updatedCondition, retrievedUpdatedCondition);

        //attempt to update a rule condition that doesn't exist
        try{
            ruleConditionsApi.updateRuleCondition(projectId, workflowId, ruleId, ThreadLocalRandom.current().nextLong(),
                    updatedCondition);
            Assert.fail("Expecting exception to have been thrown when updating rule condition that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition"), "Expecting message to indicate could not find rule condition. Message: "
                    +e.getMessage());
        }
    }

    @Test(description = "Creates some rule conditions and retrieves them through paging.")
    public void getRuleConditions() throws ApiException {
        int numberOfConditionsToCreate = 26;
        List<ExistingCondition> createdConditions = new LinkedList<>();

        ExistingWorkflow createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = createdWorkflow.getId();
        ExistingProcessingRule createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        long ruleId = createdRule.getId();

        for(int createdConditionCounter = 0; createdConditionCounter < numberOfConditionsToCreate;
            createdConditionCounter++) {
            Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
            createdConditions.add(ruleConditionsApi.createRuleCondition(projectId, workflowId, ruleId,
                    conditionToCreate));
        }

        //page through the conditions
        int pageSize = 5;
        pageThroughConditions(pageSize, workflowId, ruleId, createdConditions, numberOfConditionsToCreate);

        //create another processing rule and verify that conditions created under it are correctly pageable
        numberOfConditionsToCreate = 23;
        createdConditions = new LinkedList<>();
        createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        ruleId = createdRule.getId();

        for(int createdConditionCounter = 0; createdConditionCounter < numberOfConditionsToCreate;
            createdConditionCounter++) {
            Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
            createdConditions.add(ruleConditionsApi.createRuleCondition(projectId, workflowId, ruleId,
                    conditionToCreate));
        }
        pageSize = 4;
        pageThroughConditions(pageSize, workflowId, ruleId, createdConditions, numberOfConditionsToCreate);

        //create another workflow, add conditions under it, then verify only those conditions appropriate are returned
        createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        workflowId = createdWorkflow.getId();
        numberOfConditionsToCreate = 25;
        createdConditions = new LinkedList<>();
        createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        ruleId = createdRule.getId();

        for(int createdConditionCounter = 0; createdConditionCounter < numberOfConditionsToCreate;
            createdConditionCounter++) {
            Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
            createdConditions.add(ruleConditionsApi.createRuleCondition(projectId, workflowId, ruleId,
                    conditionToCreate));
        }
        pageSize = 100;
        pageThroughConditions(pageSize, workflowId, ruleId, createdConditions, numberOfConditionsToCreate);
    }

    @Test(description = "Creates some rule conditions then deletes one of them.")
    public void deleteRuleCondition() throws ApiException {
        ExistingWorkflow createdWorkflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = createdWorkflow.getId();
        ExistingProcessingRule createdRule = ObjectsCreator.createProcessingRule(projectId,
                workflowId, null);
        long ruleId = createdRule.getId();

        Condition conditionToCreate_1 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_1 = ruleConditionsApi.createRuleCondition(projectId,
                workflowId, ruleId, conditionToCreate_1);

        Condition conditionToCreate_2 = ObjectsInitializer.initializeCondition(null);
        ExistingCondition createdCondition_2 = ruleConditionsApi.createRuleCondition(projectId,
                workflowId, ruleId, conditionToCreate_2);

        //delete first condition
        ruleConditionsApi.deleteRuleCondition(projectId, workflowId, ruleId, createdCondition_1.getId());

        ExistingConditions conditionsPage = ruleConditionsApi.getRuleConditions(projectId,
                workflowId, ruleId, 1, 100);
        Assert.assertEquals((int)conditionsPage.getTotalHits(), 1,
                "Total Hits should be one after deleting a condition.");
        Assert.assertEquals(conditionsPage.getConditions().size(), 1,
                "One condition should be returned in the results.");

        ExistingCondition remainingCondition = conditionsPage.getConditions().get(0);
        compareConditions(createdCondition_2, remainingCondition);

        Assert.assertEquals(remainingCondition.getId(), createdCondition_2.getId(),
                "ID of remaining condition should be the second condition that was created.");
        try{
            ruleConditionsApi.getRuleCondition(projectId,
                    workflowId, ruleId, createdCondition_1.getId());
            Assert.fail("Expecting exception to be thrown when trying to retrieve deleted condition.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition"),
                    "Exception message should contain expected message about not finding conditon. Message: "+e.getMessage());
        }
        //attempt to delete a rule condition that doesn't exist
        try {
            ruleConditionsApi.deleteRuleCondition(projectId, workflowId, ruleId, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expecting exception to have been thrown when deleting rule condition that doesn't exist.");
        } catch (ApiException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition"),
                    "Expecting message to indicate could not find rule condition. Message: " + e.getMessage());
        }

        //attempt to delete a rule condition using incorrect rule ID
        ExistingProcessingRule rule_2 = ObjectsCreator.createProcessingRule(projectId, workflowId, null);
        long ruleId_2 = rule_2.getId();
        try {
            ruleConditionsApi.deleteRuleCondition(projectId, workflowId, ruleId_2, createdCondition_2.getId());
            Assert.fail("Expected exception to be thrown trying to delete second rule condition using incorrect rule ID.");
        } catch (ApiException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to find matching Condition"),
                    "Expected message to indicate could not find second rule condition. Message: " + e.getMessage());
        }
        //verify that the condition was not deleted
        ExistingCondition retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId, ruleId,
                createdCondition_2.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition_2.getId(),
                "ID on retrieved condition after failed delete should be ID sent in request.");
        compareConditions(createdCondition_2, retrievedCondition);

    }

    @Test(description = "Creates a rule condition with a number condition, retrieves it and updates it.")
    public void createGetAndUpdateNumberRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        NumberConditionAdditional numberConditionAdditional = ObjectsInitializer.initializeNumberConditionAdditional();
        conditionToCreate.setAdditional(numberConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        numberConditionAdditional = ObjectsInitializer.initializeNumberConditionAdditional();
        numberConditionAdditional.setOperator(NumberConditionAdditional.OperatorEnum.GT);
        numberConditionAdditional.setOrder(200);
        conditionToUpdate.setAdditional(numberConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates a rule condition with a number condition that has a 64-bit number and retrieves it.")
    public void createAndGetNumberInt64RuleCondition() throws ApiException {
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

        createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);
    }

    @Test(description = "Creates, retrieves and updates a rule condition with a regex condition.")
    public void createGetAndUpdateRegexRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        RegexConditionAdditional regexConditionAdditional = ObjectsInitializer.initializeRegexConditionAdditional();
        conditionToCreate.setAdditional(regexConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        regexConditionAdditional = ObjectsInitializer.initializeRegexConditionAdditional();
        conditionToUpdate.setAdditional(regexConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates rule condition with a date condition, retrieves it and updates it.")
    public void createGetDateRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        DateConditionAdditional dateConditionAdditional = ObjectsInitializer.initializeDateConditionAdditional();
        conditionToCreate.setAdditional(dateConditionAdditional);

        long idOfCreatedCondition = createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);

        Condition conditionToUpdate = ObjectsInitializer.initializeCondition(null);
        dateConditionAdditional = ObjectsInitializer.initializeDateConditionAdditional();
        conditionToUpdate.setAdditional(dateConditionAdditional);
        updateAndRetrieveCondition(workflowId_1, ruleId_1, idOfCreatedCondition, conditionToUpdate);
    }

    @Test(description = "Creates rule condition with a not condition and retrieves it.")
    public void createAndGetNotRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();

        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        NotConditionAdditional notConditionAdditional = ObjectsInitializer.initializeNotConditionAdditional(null);
        conditionToCreate.setAdditional(notConditionAdditional);

        createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);
    }

    @Test(description = "Creates rule condition with an exists condition and retrieves it.")
    public void createAndGetExistsRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();
        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        ExistsConditionAdditional existsConditionAdditional = ObjectsInitializer.initializeExistsConditionAdditional();
        conditionToCreate.setAdditional(existsConditionAdditional);

        createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);
    }

    @Test(description = "Creates rule condition with a boolean condition and retrieves it.")
    public void createAndGetBooleanRuleCondition() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();
        ExistingAction action_1 = ObjectsCreator.createAction(projectId, workflowId_1, ruleId_1, 100, null, null);
        long actionId_1 = action_1.getId();
        Condition conditionToCreate = ObjectsInitializer.initializeCondition(null);
        BooleanConditionAdditional booleanConditionAdditional = ObjectsInitializer.initializeBooleanConditionAdditional(null);

        conditionToCreate.setAdditional(booleanConditionAdditional);
        createAndRetrieveCondition(workflowId_1, ruleId_1, conditionToCreate);
    }

    private long createAndRetrieveCondition(long workflowId, long ruleId, Condition conditionToCreate) throws ApiException {
        ExistingCondition createdCondition = ruleConditionsApi.createRuleCondition(projectId, workflowId,
                ruleId, conditionToCreate);
        compareConditions(conditionToCreate, createdCondition);

        ExistingCondition retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId, ruleId,
                createdCondition.getId());
        Assert.assertEquals(retrievedCondition.getId(), createdCondition.getId(),
                "ID of retrieved condition should match that requested.");
        compareConditions(createdCondition, retrievedCondition);
        return retrievedCondition.getId();
    }

    private void updateAndRetrieveCondition(long workflowId, long ruleId, long conditionId,
                                            Condition conditionToUpdate) throws ApiException {
        ruleConditionsApi.updateRuleCondition(projectId, workflowId, ruleId, conditionId,
                conditionToUpdate);

        ExistingCondition retrievedCondition = ruleConditionsApi.getRuleCondition(projectId, workflowId, ruleId,
                conditionId);
        Assert.assertEquals((long) retrievedCondition.getId(), conditionId,
                "ID of retrieved condition after update should match that requested.");
        compareConditions(conditionToUpdate, retrievedCondition);
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

    private void pageThroughConditions(int pageSize, long workflowId, long ruleId,
                                       List<ExistingCondition> conditionsToFind, int expectedNumberOfConditions)
            throws ApiException {
        int pageNum = 1;
        int conditionsSoFarCount = 0;
        while(true){
            ExistingConditions conditionsPage = ruleConditionsApi.getRuleConditions(projectId,
                    workflowId, ruleId, pageNum, pageSize);
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
        ExistingConditions expectedEmptyGetResult = ruleConditionsApi.getRuleConditions(projectId,
                workflowId, ruleId, pageNum, pageSize);

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
}
