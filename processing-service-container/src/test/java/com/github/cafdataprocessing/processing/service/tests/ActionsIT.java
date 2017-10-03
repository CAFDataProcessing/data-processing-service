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
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionsApi;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.model.*;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.ExampleActionTypeDefinition;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsCreator;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Integration tests for Action API paths in the Data Processing Service.
 */
public class ActionsIT {
    private String projectId;
    private static ActionsApi actionsApi;
    private static ProcessingRulesApi processingRulesApi;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void setup() throws Exception {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        actionsApi = new ActionsApi(apiClient);
        processingRulesApi = new ProcessingRulesApi(apiClient);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Before every test generate a new project ID to avoid results from previous tests affecting subsequent tests.
     */
    @BeforeMethod
    public void intializeProjectId(){
        projectId = UUID.randomUUID().toString();
    }

    @Test(description = "Creates some actions and retrieves them individually.")
    public void createAndGetAction() throws ApiException {
        //create workflow and rule to add actions under
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        Long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflow_1.getId(), null);
        Long ruleId_1 = rule_1.getId();

        int actionOrder_1 = 100;
        Action actionToCreate_1 = initializeActionWithNewType(actionOrder_1);
        ExistingAction createdAction_1 = actionsApi.createAction(projectId, workflowId_1, ruleId_1, actionToCreate_1);
        compareActions(actionToCreate_1, createdAction_1);

        int actionOrder_2 = 200;
        Action actionToCreate_2 = initializeActionWithNewType(actionOrder_2);
        ExistingAction createdAction_2 = actionsApi.createAction(projectId, workflowId_1, ruleId_1, actionToCreate_2);
        compareActions(actionToCreate_2, createdAction_2);

        //retrieve actions
        ExistingAction retrievedAction_1 = actionsApi.getAction(projectId, workflowId_1, ruleId_1, createdAction_1.getId());
        Assert.assertEquals(retrievedAction_1.getId(), createdAction_1.getId(),
                "Retrieved first action should have the ID sent in request.");
        compareActions(actionToCreate_1, retrievedAction_1);

        ExistingAction retrievedAction_2 = actionsApi.getAction(projectId, workflowId_1, ruleId_1, createdAction_2.getId());
        Assert.assertEquals(retrievedAction_2.getId(), createdAction_2.getId(),
                "Retrieved second action should have the ID sent in request.");
        compareActions(actionToCreate_2, retrievedAction_2);

        //verify that trying to retrieve an action that doesn't exist fails as expected
        try{
            actionsApi.getAction(projectId, workflowId_1, ruleId_1, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception to be thrown when retrieving action by ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("No matching Action with ID"),
                    "Expected exception message to convey that action couldn't be found. Message: "+e.getMessage());
        }

        //create an action under a different rule
        ExistingProcessingRule rule_2 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_2 = rule_2.getId();
        int actionOrder_3 = 100;
        Action actionToCreate_3 = initializeActionWithNewType(actionOrder_3);
        ExistingAction createdAction_3 = actionsApi.createAction(projectId, workflowId_1, ruleId_2, actionToCreate_3);
        compareActions(actionToCreate_3, createdAction_3);

        //verify that this new action cannot be retrieved from first rule
        try{
            actionsApi.getAction(projectId, workflowId_1, ruleId_1, createdAction_3.getId());
            Assert.fail("Expected exception to be thrown when retrieving action from incorrect rule.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("No matching Action with ID:"),
                    "Expected exception message to convey that action couldn't be found on this rule. Message: "+e.getMessage());
        }

        //verify that the new action is retrieved when using the second rule and is as expected.
        ExistingAction retrievedAction_3 = actionsApi.getAction(projectId, workflowId_1, ruleId_2, createdAction_3.getId());
        Assert.assertEquals(retrievedAction_3.getId(), createdAction_3.getId(),
                "Retrieved third action should have the ID sent in request.");
        compareActions(actionToCreate_3, retrievedAction_3);

        //create a rule under a different workflow
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_2 = workflow_2.getId();
        ExistingProcessingRule processingRule_3 = ObjectsCreator.createProcessingRule(projectId, workflowId_2, null);
        long ruleId_3 = processingRule_3.getId();
        Action actionToCreate_4 = initializeActionWithNewType(100);
        ExistingAction createdAction_4 = actionsApi.createAction(projectId, workflowId_2, ruleId_3, actionToCreate_4);
        compareActions(actionToCreate_4, createdAction_4);

        //verify that this new action cannot be retrieved using first rule ID
        try{
            actionsApi.getAction(projectId, workflowId_2, ruleId_1, createdAction_4.getId());
            Assert.fail("Expected exception to be thrown when retrieving action from incorrect rule under second workflow.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID"),
                    "Expected exception message to convey that action couldn't be found on this rule under second workflow. Message: "+e.getMessage());
        }

        //verify that this new action cannot be retrieved using first workflow and correct rule ID
        try{
            actionsApi.getAction(projectId, workflowId_1, ruleId_3, createdAction_4.getId());
            Assert.fail("Expected exception to be thrown when retrieving action using correct rule but first workflow.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID:"),
                    "Expected exception message to convey that action couldn't be found due to passing first workflow. Message: "+e.getMessage());
        }

        //verify that the new action is retrieved when using the second rule and is as expected.
        ExistingAction retrievedAction_4 = actionsApi.getAction(projectId, workflowId_2, ruleId_3, createdAction_4.getId());
        Assert.assertEquals(retrievedAction_4.getId(), createdAction_4.getId(),
                "Retrieved fourth action should have the ID sent in request.");
        compareActions(actionToCreate_4, retrievedAction_4);
    }

    @Test(description = "Creates some actions and updates ones of them.")
    public void updateAction() throws ApiException {
        ExistingWorkflow workflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = workflow.getId();
        ExistingProcessingRule rule = ObjectsCreator.createProcessingRule(projectId, workflowId, null);
        long ruleId = rule.getId();

        Action actionToCreate_1 = initializeActionWithNewType(100);
        ExistingAction createdAction_1 = actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate_1);

        Action actionToCreate_2 = initializeActionWithNewType(200);
        ExistingAction createdAction_2 = actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate_2);

        Action actionToUpdate = initializeActionWithNewType(300);
        actionsApi.updateAction(projectId, workflowId, ruleId, createdAction_1.getId(), actionToUpdate);
        //retrieve the updated action and verify it is correct. Also retrieve the other action and verify it is as expected.

        ExistingAction updatedAction = actionsApi.getAction(projectId, workflowId, ruleId, createdAction_1.getId());
        Assert.assertEquals(updatedAction.getId(), createdAction_1.getId(),
                "ID of action returned should match the ID in the request.");
        compareActions(actionToUpdate, updatedAction);

        ExistingAction retrievedAction = actionsApi.getAction(projectId, workflowId, ruleId, createdAction_2.getId());
        Assert.assertEquals(retrievedAction.getId(), createdAction_2.getId(),
                "ID of second action returned should match the ID in the request.");
        compareActions(actionToCreate_2, retrievedAction);

        try{
            actionsApi.updateAction(projectId, workflowId, ruleId, ThreadLocalRandom.current().nextLong(), updatedAction);
            Assert.fail("Expected exception when trying to update an action that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Could not find Action with ID"),
                    "Expected exception message to state unable to find the action. Message: "+e.getMessage());
        }

        //try to update action passing incorrect workflow parent
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);
        try{
            actionsApi.updateAction(projectId, workflow_2.getId(), ruleId, createdAction_2.getId(), updatedAction);
            Assert.fail("Expected exception when trying to update action passing incorrect workflow.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID"),
                    "Expected exception message to state unable to find rule on workflow. Message: "+e.getMessage());
        }
        //verify that action wasn't updated
        retrievedAction = actionsApi.getAction(projectId, workflowId, ruleId, createdAction_2.getId());
        Assert.assertEquals(retrievedAction.getId(), createdAction_2.getId(),
                "ID of second action returned should match the ID in the request.");
        compareActions(actionToCreate_2, retrievedAction);

        //try to update action passing incorrect processing rule parent
        ExistingProcessingRule rule_2 = ObjectsCreator.createProcessingRule(projectId, workflowId, 200);
        try{
            actionsApi.updateAction(projectId, workflowId, rule_2.getId(), createdAction_2.getId(), updatedAction);
            Assert.fail("Expected exception when trying to update action passing incorrect rule parent.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("There are no Actions on Rule with ID"),
                    "Expected exception message to state there are no actions on the rule. Message: "+e.getMessage());
        }
        //add an action to the new rule
        ObjectsCreator.createAction(projectId, workflowId, rule_2.getId(), 100, null, null);
        try{
            actionsApi.updateAction(projectId, workflowId, rule_2.getId(), createdAction_2.getId(), updatedAction);
            Assert.fail("Expected exception when trying to update action passing incorrect rule parent when actions exist on the incorrect parent.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Could not find Action with ID"),
                    "Expected exception message to state the action couldn't be found on this rule. Message: "+e.getMessage());
        }

        //verify that action wasn't updated
        retrievedAction = actionsApi.getAction(projectId, workflowId, ruleId, createdAction_2.getId());
        Assert.assertEquals(retrievedAction.getId(), createdAction_2.getId(),
                "ID of second action returned should match the ID in the request.");
        compareActions(actionToCreate_2, retrievedAction);
    }

    @Test(description = "Creates some actions, then deletes one of them.")
    public void deleteAction() throws ApiException {
        ExistingWorkflow workflow = ObjectsCreator.createWorkflow(projectId);
        long workflowId = workflow.getId();
        ExistingProcessingRule rule = ObjectsCreator.createProcessingRule(projectId, workflowId, null);
        long ruleId = rule.getId();

        //create some actions
        Action actionToCreate_1 = initializeActionWithNewType(100);
        ExistingAction createdAction_1 = actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate_1);

        Action actionToCreate_2 = initializeActionWithNewType(200);
        ExistingAction createdAction_2 = actionsApi.createAction(projectId, workflowId, ruleId, actionToCreate_2);

        //delete first action
        actionsApi.deleteAction(projectId, workflowId, ruleId, createdAction_1.getId());

        //verify the action cannot be retrieved after delete
        try{
            actionsApi.getAction(projectId, workflowId, ruleId, createdAction_1.getId());
            Assert.fail("Expected exception message to be thrown when trying to retrieve deleted action.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("No matching Action with ID"),
                    "Expected exception message to state that action could not be found. Message: "+e.getMessage());
        }

        //retrieve actions and check that only one is now present
        ExistingActions actionsPage = actionsApi.getActions(projectId, workflowId, ruleId, 1, 100);
        Assert.assertEquals((int)actionsPage.getTotalHits(), 1, "Total Hits should be one after deleting an action.");
        Assert.assertEquals(actionsPage.getActions().size(), 1, "One action should be returned in the results.");
        ExistingAction remainingAction = actionsPage.getActions().get(0);
        Assert.assertEquals(remainingAction.getId(), createdAction_2.getId(),
                "ID of remaining action should be the second action created.");
        compareActions(actionToCreate_2, remainingAction);
    }

    @Test(description = "Creates actions and retrieves them through paging.")
    public void getActions() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();

        int numberOfActionsToCreate = 21;
        List<ExistingAction> createdActions = createMultipleActions(numberOfActionsToCreate, workflowId_1, ruleId_1);

        //take a copy of this for use later
        List<ExistingAction> copyOfFirstActions = new LinkedList<>();
        copyOfFirstActions.addAll(createdActions);

        //page through actions. Should find all that were created.
        int pageSize = 5;
        pageThroughActions(pageSize, workflowId_1, ruleId_1, createdActions, createdActions.size());

        //create actions under a different rule
        ExistingProcessingRule rule_2 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_2 = rule_2.getId();
        numberOfActionsToCreate = 18;
        createdActions = createMultipleActions(numberOfActionsToCreate, workflowId_1, ruleId_2);
        pageSize = 7;
        pageThroughActions(pageSize, workflowId_1, ruleId_2, createdActions, createdActions.size());

        //create another action and verify that paging updates
        Action additionalAction = initializeActionWithNewType(5000);
        ExistingAction createdAction = actionsApi.createAction(projectId, workflowId_1, ruleId_1, additionalAction);
        copyOfFirstActions.add(createdAction);
        pageSize = 100;
        pageThroughActions(pageSize, workflowId_1, ruleId_1, copyOfFirstActions, copyOfFirstActions.size());

        //verify that passing incorrect parent information causes an exception
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);
        try{
            actionsApi.getActions(projectId, workflow_2.getId(), ruleId_2, 1, 100);
            Assert.fail("Expected exception to be thrown trying to retrieve actions passing incorrect workflow parent.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID"),
                    "Expected exception message to mention problem with workflow and rule parents. Message: "+e.getMessage());
        }
    }
    
    @Test(description = "Creates actions, updates their parent rule and checks the actions still exist them through paging. A bug previously"
        + " existedwhere updating a rule would delete actions under it.")
    public void updateRuleAndCheckActions() throws ApiException {
        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule rule_1 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, null);
        long ruleId_1 = rule_1.getId();

        int numberOfActionsToCreate = 21;
        List<ExistingAction> createdActions = createMultipleActions(numberOfActionsToCreate, workflowId_1, ruleId_1);

        //take a copy of this for use later
        List<ExistingAction> copyOfFirstActions = new LinkedList<>();
        copyOfFirstActions.addAll(createdActions);

        //page through actions. Should find all that were created.
        int pageSize = 5;
        pageThroughActions(pageSize, workflowId_1, ruleId_1, createdActions, createdActions.size());

        //update processing rule
        rule_1.setDescription("Updated description.");
        processingRulesApi.updateRule(projectId, workflowId_1, ruleId_1, rule_1);
        
        //check actions are still available
        pageThroughActions(pageSize, workflowId_1, ruleId_1, copyOfFirstActions, copyOfFirstActions.size());
    }

    private void pageThroughActions(int pageSize, long workflowId, long ruleId,
                                    List<ExistingAction> actionsToFind,
                                    int expectedNumberOfActions) throws ApiException {
        int actionsSoFarCount = 0;
        int pageNum = 1;
        while(true){
            ExistingActions actionsPage = actionsApi.getActions(projectId,
                    workflowId, ruleId, pageNum, pageSize);
            Assert.assertEquals((long)actionsPage.getTotalHits(), expectedNumberOfActions,
                    "Total hits should be the same as the expected number of actions.");

            List<ExistingAction> retrievedActions = actionsPage.getActions();
            if(pageNum*pageSize <= expectedNumberOfActions){
                //until we get to the page that includes the last result (or go beyond the number available)
                //there should always be 'pageSize' number of results returned
                Assert.assertEquals(retrievedActions.size(), pageSize,
                        "Expecting full page of action results.");
            }
            //remove returned actions from list of actions to find
            checkActionsReturned(actionsPage, actionsToFind);
            //increment page num so that next call retrieves next page
            pageNum++;
            actionsSoFarCount += retrievedActions.size();
            if(actionsSoFarCount > expectedNumberOfActions){
                Assert.fail("More actions encountered than expected.");
            }
            else if(actionsSoFarCount == expectedNumberOfActions){
                Assert.assertTrue(actionsToFind.isEmpty(),
                        "After encountering the expected number of actions there should be no more actions that we are searching for.");
                break;
            }
        }
    }

    private void checkActionsReturned(ExistingActions actionsPage, List<ExistingAction> actionsToFind){
        for(ExistingAction retrievedAction: actionsPage.getActions()){
            Optional<ExistingAction> foundAction = actionsToFind.stream()
                    .filter(filterAction -> filterAction.getId().equals(retrievedAction.getId())).findFirst();
            if(foundAction.isPresent()) {
                compareActions(foundAction.get(), retrievedAction);
                //remove from the list of actions for next check
                actionsToFind.remove(foundAction.get());
            }
            else{
                Assert.fail("An unexpected action was returned.");
            }
        }
    }

    private List<ExistingAction> createMultipleActions(int numberOfActionsToCreate, long workflowId, long ruleId) throws ApiException {
        List<ExistingAction> createdActions = new LinkedList<>();
        int runningOrder = 100;
        for(int numberOfActionsCreated = 0; numberOfActionsCreated < numberOfActionsToCreate; numberOfActionsCreated++){
            //create a processing rule
            Action newAction = initializeActionWithNewType(runningOrder++);
            ExistingAction createdAction = actionsApi.createAction(projectId,
                    workflowId, ruleId, newAction);
            createdActions.add(createdAction);
        }
        return createdActions;
    }

    /**
     * Creates a new action type then initializes an action using that new type.
     * @param order Order to use on the action.
     * @return The initialized Action object.
     * @throws ApiException
     */
    private Action initializeActionWithNewType(int order) throws ApiException {
        ExistingActionType type = ObjectsCreator.createActionType(projectId);
        ExampleActionTypeDefinition typeDefinition = mapper.convertValue(type.getDefinition(),
                ExampleActionTypeDefinition.class);
        return ObjectsInitializer.initializeAction(order, type.getId(),
                typeDefinition.generateSettingsForType());
    }

    private void compareActions(Action expectedAction, ExistingAction retrievedAction){
        Assert.assertEquals(retrievedAction.getName(), expectedAction.getName(),
                "Name should be as expected on retrieved action.");
        Assert.assertEquals(retrievedAction.getDescription(), expectedAction.getDescription(),
                "Description should be as expected on retrieved action.");
        Assert.assertEquals(retrievedAction.getOrder(), expectedAction.getOrder(),
                "Order should be as expected on retrieved action.");
        Assert.assertEquals(retrievedAction.getTypeId(), expectedAction.getTypeId(),
                "Type ID should be as expected on retrieved action.");
        ExampleActionTypeDefinition.compareProperties((LinkedHashMap<String, Object>) retrievedAction.getSettings(),
                (LinkedHashMap<String, Object>) expectedAction.getSettings());
    }
}
