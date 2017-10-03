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

import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsCreator;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.BaseProcessingRule;
import com.github.cafdataprocessing.processing.service.client.model.ExistingProcessingRule;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.processing.service.client.model.ProcessingRules;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Integration tests for Processing Rule API paths in the Data Processing Service.
 */
public class ProcessingRulesIT {
    private static ProcessingRulesApi processingRulesApi;
    private static WorkflowsApi workflowsApi;
    private String projectId;

    @BeforeClass
    public static void setup() throws Exception {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        workflowsApi = new WorkflowsApi(apiClient);
        processingRulesApi = new ProcessingRulesApi(apiClient);
    }

    /**
     * Before every test generate a new project ID to avoid results from previous tests affecting subsequent tests.
     */
    @BeforeMethod
    public void intializeProjectId(){
        projectId = UUID.randomUUID().toString();
    }

    @Test(description = "Creates some processing rules.")
    public void createProcesingRule() throws ApiException {
        Integer processingRulePriority = 1;
        BaseProcessingRule processingRule_1 = ObjectsInitializer.initializeProcessingRule(processingRulePriority);

        ExistingWorkflow workflow = ObjectsCreator.createWorkflow(projectId);
        ExistingProcessingRule createdRule_1 = processingRulesApi.createRule(projectId, workflow.getId(), processingRule_1);
        compareProcessingRules(processingRule_1, createdRule_1);

        Integer processingRulePriority_2 = 2;
        BaseProcessingRule processingRule_2 = ObjectsInitializer.initializeProcessingRule(processingRulePriority_2);
        ExistingProcessingRule createdRule_2 = processingRulesApi.createRule(projectId, workflow.getId(), processingRule_2);
        compareProcessingRules(processingRule_2, createdRule_2);

        //test that omitting Priority causes it to automatically be set to one more than current highest priority on the workflow
        BaseProcessingRule processingRule_3 = ObjectsInitializer.initializeProcessingRule(null);
        ExistingProcessingRule createdRule_3 = processingRulesApi.createRule(projectId, workflow.getId(),
                processingRule_3);
        compareProcessingRules(processingRule_3, createdRule_3, processingRulePriority_2 + 1);

        //create rule under a different workflow and verify a null priority results in priority of 1 being set
        workflow = ObjectsCreator.createWorkflow(projectId);
        BaseProcessingRule processingRule_4 = ObjectsInitializer.initializeProcessingRule(null);
        ExistingProcessingRule createdRule_4 = processingRulesApi.createRule(projectId, workflow.getId(), processingRule_4);
        compareProcessingRules(processingRule_4, createdRule_4, 1);

        //test that specifying same priority as an existing processing rule causes the created processing rule to return the priority it specified
        BaseProcessingRule classificationRule_5 = ObjectsInitializer.initializeProcessingRule(1);
        ExistingProcessingRule createdRule_5 = processingRulesApi.createRule(projectId, workflow.getId(), classificationRule_5);
        compareProcessingRules(classificationRule_5, createdRule_5, 1);
    }

    @Test(description = "Creates some processing rules and then retrieves them individually.")
    public void getProcessingRule() throws ApiException {
        Integer processingRulePriority_1 = 1;
        BaseProcessingRule processingRule_1 = ObjectsInitializer.initializeProcessingRule(processingRulePriority_1);

        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        Long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule createdRule_1 = processingRulesApi.createRule(projectId, workflowId_1, processingRule_1);

        //Specify no priority for second rule to ensure that it is defaulted to next available priority.
        BaseProcessingRule processingRule_2 = ObjectsInitializer.initializeProcessingRule(null);
        ExistingProcessingRule createdRule_2 = processingRulesApi.createRule(projectId, workflowId_1, processingRule_2);

        //retrieve the processing rules
        ExistingProcessingRule retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1, createdRule_1.getId());
        Assert.assertEquals(retrievedProcessingRule.getId(), createdRule_1.getId(),
                "ID of Processing Rule returned does not match ID requested. 1st Rule.");
        compareProcessingRules(createdRule_1, retrievedProcessingRule);

        retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1, createdRule_2.getId());
        Assert.assertEquals(retrievedProcessingRule.getId(), createdRule_2.getId(),
                "ID of Processing Rule returned does not match ID requested. 2nd Rule.");
        compareProcessingRules(createdRule_2, retrievedProcessingRule, 2);

        //verify that adding a processing rule with an existing priority causes existing processing rule priorities
        //to be incremented
        {
            BaseProcessingRule processingRule_3 = ObjectsInitializer.initializeProcessingRule(1);
            ExistingProcessingRule createdRule_3 = processingRulesApi.createRule(projectId,
                    workflowId_1, processingRule_3);

            //retrieve the existing processing rules and verify their priorities are as expected
            retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1,
                    createdRule_3.getId());
            compareProcessingRules(processingRule_3, retrievedProcessingRule, 1);

            retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1,
                    createdRule_1.getId());
            compareProcessingRules(processingRule_1, retrievedProcessingRule, 2);

            retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1,
                    createdRule_2.getId());
            compareProcessingRules(processingRule_2, retrievedProcessingRule, 3);

        }
        //create another workflow and store a new processing rule on it
        {
            ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);

            Long workflowId_2 = workflow_2.getId();
            BaseProcessingRule processingRule_3 = ObjectsInitializer.initializeProcessingRule(null);
            ExistingProcessingRule createdRule_3 = processingRulesApi.createRule(projectId, workflowId_2,
                    processingRule_3);

            //verify that rule can be retrieved from correct workflow
            retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_2, createdRule_3.getId());
            compareProcessingRules(createdRule_3, retrievedProcessingRule, 1);

            //verify that trying to retrieve processing rule from non-parent workflow results in error
            try {
                retrievedProcessingRule = processingRulesApi.getRule(projectId, workflowId_1, createdRule_3.getId());
                Assert.fail("Exception was not thrown when requesting processing rule with non parent workflow ID.");
            } catch (ApiException e) {
                Assert.assertTrue(e.getMessage().contains("Unable to find entry for Rule with ID"),
                        "Response on exception should describe that processing rule could not be found on the Workflow. Response was: "+e.getMessage());
            }
        }
    }

    @Test(description = "Creates some processing rules then retrieves them as pages.")
    public void getProcessingRules() throws ApiException {
        //creates rules under different workflows to verify there is no crossover in the results.
        int numberOfRulesToCreate_1 = 21;
        int numberOfRulesToCreate_2 = 28;

        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);

        List<ExistingProcessingRule> createdRules_1 = createMultipleProcessingRules(numberOfRulesToCreate_1, workflow_1.getId());
        List<ExistingProcessingRule> createdRules_2 = createMultipleProcessingRules(numberOfRulesToCreate_2, workflow_2.getId());

        //page through processing rules on first workflow. Should find all that were created.
        int pageSize = 5;
        pageThroughProcessingRules(pageSize, workflow_1.getId(), createdRules_1, createdRules_1.size());
        //changing page size to verify the parameter is respected
        pageSize = 4;
        pageThroughProcessingRules(pageSize, workflow_2.getId(), createdRules_2, createdRules_2.size());
    }

    @Test(description = "Creates a processing rule then updates it.")
    public void updateProcessingRule() throws ApiException {
        Integer rulePriority_1 = 1;
        BaseProcessingRule rule_1 = ObjectsInitializer.initializeProcessingRule(rulePriority_1);

        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        Long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule createdRule_1 = processingRulesApi.createRule(projectId, workflowId_1, rule_1);

        Integer rulePriority_2 = 2;
        BaseProcessingRule rule_2 = ObjectsInitializer.initializeProcessingRule(rulePriority_2);
        ExistingProcessingRule createdRule_2 = processingRulesApi.createRule(projectId, workflowId_1, rule_2);

        //update first processing rule properties, using the priority of the 2nd processing rule.
        rule_1 = ObjectsInitializer.initializeProcessingRule(rulePriority_2);
        processingRulesApi.updateRule(projectId,
                workflowId_1, createdRule_1.getId(), rule_1);

        ExistingProcessingRule updatedRule_1 = processingRulesApi.getRule(projectId, workflowId_1,
                createdRule_1.getId());
        compareProcessingRules(rule_1, updatedRule_1);;

        //verify that second processing rule priority was not updated as part of updating the first processing rule
        ExistingProcessingRule updatedRule_2 = processingRulesApi.getRule(projectId, workflowId_1,
                createdRule_2.getId());
        compareProcessingRules(rule_2, updatedRule_2);

        //try to update a rule that doesn't exist
        try{
            processingRulesApi.updateRule(projectId, workflowId_1, ThreadLocalRandom.current().nextLong(), rule_1);
            Assert.fail("Expected exception to have been thrown trying to update processing rule with an ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID"),
                    "Expecting exception message to mention unable to find rule. Messages was: "+e.getMessage());
        }

        //try to update a rule passing incorrect parent workflow ID
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);
        Long workflowId_2 = workflow_2.getId();
        BaseProcessingRule wrongParentUpdate = ObjectsInitializer.initializeProcessingRule(300);

        try{
            processingRulesApi.updateRule(projectId, workflowId_2, createdRule_2.getId(), wrongParentUpdate);
            Assert.fail("Expected exception to have been thrown trying to update rule with incorrect parent.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID"),
                    "Expected exception message to mention unable to find rule on the workflow. Message was: "+e.getMessage());
        }
    }

    @Test(description = "Creates some processing rules and then deletes them.")
    public void deleteProcessingRule() throws ApiException {
        Integer rulePriority_1 = 1;
        BaseProcessingRule rule_1 = ObjectsInitializer.initializeProcessingRule(rulePriority_1);

        ExistingWorkflow workflow_1 = ObjectsCreator.createWorkflow(projectId);
        Long workflowId_1 = workflow_1.getId();
        ExistingProcessingRule createdRule_1 = processingRulesApi.createRule(projectId, workflowId_1, rule_1);

        Integer rulePriority_2 = 2;
        BaseProcessingRule rule_2 = ObjectsInitializer.initializeProcessingRule(rulePriority_2);
        ExistingProcessingRule createdRule_2 = processingRulesApi.createRule(projectId, workflowId_1, rule_2);

        //delete the first processing rule
        processingRulesApi.deleteRule(projectId, workflowId_1, createdRule_1.getId());

        //verify that the processing rule no longer exists on the workflow
        ProcessingRules rulesOnWorkflowResult = processingRulesApi.getRules(projectId,
                workflowId_1, 1, 100);
        Assert.assertEquals((long) rulesOnWorkflowResult.getTotalHits(), 1,
                "Total Hits should be one after deleting a processing rule.");
        Assert.assertEquals(rulesOnWorkflowResult.getRules().size(), 1,
                "One processing rule should be returned in the results.");

        ExistingProcessingRule remainingRule = rulesOnWorkflowResult.getRules().get(0);
        compareProcessingRules(rule_2, remainingRule);

        Assert.assertEquals(remainingRule.getId(), createdRule_2.getId(),
                "ID of remaining processing rule should be the second processing rule that was created.");
        try{
            processingRulesApi.getRule(projectId, workflowId_1, createdRule_1.getId());
            Assert.fail("Expecting exception to be thrown when trying to retrieve deleted processing rule.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Rule"),
                    "Exception message should contain expected message about not finding processing rule.");
        }

        //try to delete a rule that doesn't exist
        try{
            processingRulesApi.deleteRule(projectId, workflowId_1, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception to have been thrown trying to delete processing rule with an ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Rule"),
                    "Expecting exception message to mention unable to find rule. Messages was: "+e.getMessage());
        }

        //try to delete a rule that exists but specifying the incorrect parent workflow ID
        ExistingWorkflow workflow_2 = ObjectsCreator.createWorkflow(projectId);
        long workflowId_2 = workflow_2.getId();
        ExistingProcessingRule rule_3 = ObjectsCreator.createProcessingRule(projectId, workflowId_1, 100);
        long rule3_Id = rule_3.getId();
        try{
            processingRulesApi.deleteRule(projectId, workflowId_2, rule3_Id);
            Assert.fail("Expected exception to have been thrown trying to delete processing rule having specified incorrect parent.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("not found on Workflow with ID:"),
                    "Expecting exception message to mention unable to find rule on the specified parent. Messages was: "+e.getMessage());
        }
        //verify that rule was not deleted
        ExistingProcessingRule retrievedRule = processingRulesApi.getRule(projectId, workflowId_1, rule3_Id);
        Assert.assertEquals((long)retrievedRule.getId(), rule3_Id,
                "ID on retrieved processing rule after failed delete under incorrect parent should match ID on request.");
        compareProcessingRules(rule_3, retrievedRule);
    }

    /**
     * Convenience method to issue page requests against a workflow ID searching for a specified set of processing rules.
     * @param pageSize Number of processing rules to retrieve for each page request.
     * @param workflowId The workflow that processing rules are under.
     * @param rulesToFind The processing rules that are expected to be returned by the page requests.
     *                                  Modified by this method to remove all those found.
     * @param expectedNumberOfRules The number of processing rules that should be under the workflow.
     * @throws ApiException
     */
    private void pageThroughProcessingRules(int pageSize, long workflowId,
                                                List<ExistingProcessingRule> rulesToFind,
                                                int expectedNumberOfRules) throws ApiException {
        int rulesSoFarCount = 0;
        int pageNum = 1;
        while(true){
            ProcessingRules rulesPage = processingRulesApi.getRules(projectId,
                    workflowId, pageNum, pageSize);
            Assert.assertEquals((long)rulesPage.getTotalHits(), expectedNumberOfRules,
                    "Total hits should be the same as the expected number of processing rules.");

            List<ExistingProcessingRule> retrievedRules = rulesPage.getRules();

            if(pageNum*pageSize <= expectedNumberOfRules){
                //until we get to the page that includes the last result (or go beyond the number available)
                //there should always be 'pageSize' number of results returned
                Assert.assertEquals(retrievedRules.size(), pageSize, "Expecting full page of processing rule results.");
            }
            //remove returned processing rules from list of processing rules to find
            checkProcessingRulesReturned(rulesPage, rulesToFind);
            //increment page num so that next call retrieves next page
            pageNum++;
            rulesSoFarCount += retrievedRules.size();
            if(rulesSoFarCount > expectedNumberOfRules){
                Assert.fail("More processing rules encountered than expected.");
            }
            else if(rulesSoFarCount == expectedNumberOfRules){
                Assert.assertTrue(rulesToFind.isEmpty(),
                        "After encountering the expected number of processing rules there should be no more processing rules that we are searching for.");
                break;
            }
        }
        //send a final get request and verify that nothing is returned.
        ProcessingRules expectedEmptyGetResult = processingRulesApi.getRules(projectId,
                workflowId, pageNum, pageSize);

        Assert.assertEquals((long) expectedEmptyGetResult.getTotalHits(), expectedNumberOfRules,
                "Total hits should report the expected number of processing rules even on a page outside range of results.");
        Assert.assertTrue(expectedEmptyGetResult.getRules().isEmpty(),
                "Should be no processing rules returned for page request outside expected range.");
    }

    /**
     * Search the retrieved processing rules passed for the occurrence of a set of processing rules.
     * When a match is found for a processing rule it is removed from the list of processing rules to find passed in.
     * @param retrievedRules The processing rules to search through.
     * @param rulesToFind The processing rules to search for.
     */
    private void checkProcessingRulesReturned(ProcessingRules retrievedRules,
                                        List<ExistingProcessingRule> rulesToFind){
        for(ExistingProcessingRule retrievedRule: retrievedRules.getRules()){
            Optional<ExistingProcessingRule> foundRule = rulesToFind.stream()
                    .filter(filterRule -> filterRule.getId().equals(retrievedRule.getId())).findFirst();
            if(foundRule.isPresent()) {
                compareProcessingRules(foundRule.get(), retrievedRule);
                //remove from the list of processing rules for next check
                rulesToFind.remove(foundRule.get());
            }
            else{
                Assert.fail("An unexpected processing rule was returned.");
            }
        }
    }

    /**
     * Convenience method to create a specified number of processing rules under a workflow.
     * Returns the created processing rules.
     * @param numberOfRulesToCreate The number of processing rules to create.
     * @param workflowId The ID of the workflow to create the processing rules under.
     * @return The created processing rules.
     */
    private List<ExistingProcessingRule> createMultipleProcessingRules(int numberOfRulesToCreate,
                                                                           long workflowId) throws ApiException {
        List<ExistingProcessingRule> createdRules = new LinkedList<>();
        for(int numberOfRulesCreated = 0; numberOfRulesCreated < numberOfRulesToCreate; numberOfRulesCreated++){
            //create a processing rule
            BaseProcessingRule newRule = ObjectsInitializer.initializeProcessingRule(null);
            ExistingProcessingRule createdRule = processingRulesApi.createRule(projectId,
                    workflowId, newRule);
            createdRules.add(createdRule);
        }
        return createdRules;
    }

    /**
    * Compares two processing rules.
    * @param expectedRule Processing Rule with properties set to expected values.
    * @param retrievedRule A retrieved Processing Rule to check against expected values.
    */
    private void compareProcessingRules(BaseProcessingRule expectedRule, ExistingProcessingRule retrievedRule){
        compareProcessingRules(expectedRule, retrievedRule, null);
    }

    /**
     * Compares two processing rules, allows passing in a priority to check for on the retrievedRule.
     * @param expectedRule Processing Rule with properties set to expected values.
     * @param retrievedRule A retrieved Processing Rule to check against expected values.
     * @param expectedDefaultedPriority Optional value to use to compare against retrieved Processing Rule priority.
     */
    private void compareProcessingRules(BaseProcessingRule expectedRule,
                                        ExistingProcessingRule retrievedRule,
                                            Integer expectedDefaultedPriority){
        Assert.assertEquals(retrievedRule.getName(), expectedRule.getName(),
                "Name on Processing Rule returned should match expected value.");
        Assert.assertEquals(retrievedRule.getDescription(), expectedRule.getDescription(),
                "Description on Processing Rule returned should match expected value.");
        if(expectedDefaultedPriority==null) {
            Assert.assertEquals(retrievedRule.getPriority(), expectedRule.getPriority(),
                    "Priority on Processing Rule returned should match expected value.");
        }
        else{
            Assert.assertEquals(retrievedRule.getPriority(), expectedDefaultedPriority,
                    "Priority on Processing Rule returned should have been set automatically to expected value.");
        }
    }
}
