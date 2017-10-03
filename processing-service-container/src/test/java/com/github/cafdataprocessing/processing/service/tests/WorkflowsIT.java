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
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ProcessingRulesApi;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.BaseProcessingRule;
import com.github.cafdataprocessing.processing.service.client.model.BaseWorkflow;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflows;
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
 * Integration tests for Workflow API paths in the Data Processing Service.
 */
public class WorkflowsIT {
    private static WorkflowsApi workflowsApi;
    private static ProcessingRulesApi processingRulesApi;
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

    @Test(description = "Creates a workflow then creates another.")
    public void createWorkflow() throws ApiException {
        BaseWorkflow workflow_1 = intializeWorkflow();

        ExistingWorkflow createdWorkflow = workflowsApi.createWorkflow(projectId, workflow_1);
        compareWorkflows(workflow_1, createdWorkflow);

        BaseWorkflow workflow_2 = intializeWorkflow();

        createdWorkflow = workflowsApi.createWorkflow(projectId, workflow_2);
        compareWorkflows(workflow_2, createdWorkflow);
    }

    @Test(description = "Creates some workflows and then deletes one of them.")
    public void deleteWorkflow() throws ApiException {
        BaseWorkflow workflow_1 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_1 = workflowsApi.createWorkflow(projectId, workflow_1);

        BaseWorkflow workflow_2 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_2 = workflowsApi.createWorkflow(projectId, workflow_2);

        //delete first workflow
        workflowsApi.deleteWorkflow(projectId, createdWorkflow_1.getId());
        //verify that the workflow no longer exists
        ExistingWorkflows getWorkflowsResult = workflowsApi.getWorkflows(projectId, 1, 10);
        Assert.assertEquals((int)getWorkflowsResult.getTotalHits(), 1, "Total Hits should be one after deleting a workflow.");
        Assert.assertEquals(getWorkflowsResult.getWorkflows().size(), 1, "One workflow should be returned in the results.");

        ExistingWorkflow remainingWorkflow = getWorkflowsResult.getWorkflows().get(0);
        compareWorkflows(workflow_2, remainingWorkflow);
        Assert.assertEquals(remainingWorkflow.getId(), createdWorkflow_2.getId(), "ID of remaining workflow should be the second workflow that was created.");

        try{
            workflowsApi.getWorkflow(projectId, createdWorkflow_1.getId());
            Assert.fail("Expecting exception to be thrown when trying to retrieve deleted workflow.");
        }
        catch(ApiException e)
        {
            Assert.assertTrue(e.getMessage().contains("Unable to find Workflow with ID: " + createdWorkflow_1.getId()), "Exception message should contain expected message about not finding workflow.");
        }

        //add a classification rule to the remaining workflow and then try to delete it
        BaseProcessingRule processingRule = ObjectsInitializer.initializeProcessingRule(null);
        processingRulesApi.createRule(projectId, remainingWorkflow.getId(), processingRule);

        try{
            workflowsApi.deleteWorkflow(projectId, remainingWorkflow.getId());
            Assert.fail("Exception should have been thrown when trying to delete a workflow with a processing rule under it.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to Delete. There are Rules on the Workflow."),
            "Expecting exception message to be as described.");
        }
        //verify that the workflow wasn't actually deleted
        remainingWorkflow = workflowsApi.getWorkflow(projectId, remainingWorkflow.getId());
        compareWorkflows(workflow_2, remainingWorkflow);

        //try to delete a workflow that doesn't exist.
        try{
            workflowsApi.deleteWorkflow(projectId, createdWorkflow_1.getId());
            Assert.fail("Exception should have been thrown when trying to delete a workflow that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Workflow"),
                    "Expecting exception message to mention unable to find the workflow. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates some workflows then updates one of them.")
    public void updateWorkflow() throws ApiException {
        BaseWorkflow workflow_1 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_1 = workflowsApi.createWorkflow(projectId, workflow_1);

        BaseWorkflow workflow_2 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_2 = workflowsApi.createWorkflow(projectId, workflow_2);

        BaseWorkflow updatedWorkflow = intializeWorkflow();
        workflowsApi.updateWorkflow(projectId, createdWorkflow_1.getId(), updatedWorkflow);

        //check that workflow was correctly updated
        ExistingWorkflow returnedWorkflow = workflowsApi.getWorkflow(projectId, createdWorkflow_1.getId());
        compareWorkflows(updatedWorkflow, returnedWorkflow);

        //try to update a workflow that doesn't exist.
        try{
            workflowsApi.updateWorkflow(projectId, ThreadLocalRandom.current().nextLong(), updatedWorkflow);
            Assert.fail("Exception should have been thrown when trying to update a workflow that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Workflow"),
                    "Expecting exception message to mention unable to find the workflow. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates some workflows then retrieves one of them.")
    public void getWorkflow() throws ApiException {
        BaseWorkflow workflow_1 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_1 = workflowsApi.createWorkflow(projectId, workflow_1);

        BaseWorkflow workflow_2 = intializeWorkflow();
        ExistingWorkflow createdWorkflow_2 = workflowsApi.createWorkflow(projectId, workflow_2);

        ExistingWorkflow retrievedWorkflow = workflowsApi.getWorkflow(projectId, createdWorkflow_1.getId());
        compareWorkflows(workflow_1, retrievedWorkflow);
        Assert.assertEquals(retrievedWorkflow.getId(), createdWorkflow_1.getId(), "The ID of the retrieved workflow should match the ID requested.");
    }

    @Test(description = "Creates multiple workflows and verifies that all of them are returned from page requests.")
    public void getWorkflows() throws ApiException {
        int numberOfWorkflowsToCreate = 21;
        List<ExistingWorkflow> workflowsToFind = new LinkedList<>();
        for(int numberOfWorkflowsCreated = 0; numberOfWorkflowsCreated < numberOfWorkflowsToCreate; numberOfWorkflowsCreated++){
            //create a workflow
            BaseWorkflow newWorkflow = intializeWorkflow();
            ExistingWorkflow createdWorkflow = workflowsApi.createWorkflow(projectId, newWorkflow);
            workflowsToFind.add(createdWorkflow);
        }
        //page through workflows, should find all of them by the time the end is reached
        int pageSize = 5;
        int pageNum = 1;
        int workflowsSoFarCount = 0;
        while(true) {
            ExistingWorkflows getWorkflowsResult = workflowsApi.getWorkflows(projectId, pageNum, pageSize);
            List<ExistingWorkflow> retrievedWorkflows = getWorkflowsResult.getWorkflows();
            Assert.assertEquals((int) getWorkflowsResult.getTotalHits(), numberOfWorkflowsToCreate, "Total hits should report the expected number of workflows.");

            if(pageNum*pageSize <= numberOfWorkflowsToCreate){
                //until we get to the page that includes the last result (or go beyond the number available) there should always be 'pageSize' number of results returned
                Assert.assertEquals(retrievedWorkflows.size(), pageSize, "Expecting full page of term list results.");
            }
            //remove returned workflows from the list of workflows to find
            checkWorkflowsReturned(getWorkflowsResult, workflowsToFind);

            //increment page num so that call to get workflows retrieves the next page of results
            pageNum++;

            workflowsSoFarCount += getWorkflowsResult.getWorkflows().size();
            if(workflowsSoFarCount>numberOfWorkflowsToCreate){
                Assert.fail("More workflows encountered than expected.");
            }
            if(workflowsSoFarCount==numberOfWorkflowsToCreate){
                Assert.assertTrue(workflowsToFind.isEmpty(), "After encountering the expected number of workflows there should be no more workflows that we are searching for.");
                break;
            }
        }
        //send a final get request and verify that nothing is returned.
        ExistingWorkflows expectedEmptyGetResult = workflowsApi.getWorkflows(projectId, pageNum, pageSize);

        Assert.assertEquals((int) expectedEmptyGetResult.getTotalHits(), numberOfWorkflowsToCreate, "Total hits should report the expected number of workflows even on a page outside range of results.");
        Assert.assertTrue(expectedEmptyGetResult.getWorkflows().isEmpty(), "Should be no workflows returned for page request outside expected range.");
    }

    @Test(description = "Tries to retrieve a workflow using an ID beyond the precision of JavaScript Number and verifies that that value is used to try and retrieve workflow.")
    public void getWorkflowWithInt64Id(){
        Long workflowId = 1234567890123456786L;
        String expectedErrorMessage = "Unable to find Workflow with ID: "+workflowId;

        try {
            workflowsApi.getWorkflow(projectId, workflowId);
            Assert.fail("Expected exception to have been thrown trying to retrieve workflow with incredibly high ID.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains(expectedErrorMessage),
                    "Expecting exception message to exactly match with no loss of precision in the ID. Returned message: "+e.getMessage());
        }
    }

    /**
     * Search the retrieved workflows passed for the occurrence of a set of workflow. When a match is found for a workflow it is removed from the list of workflows to find passed in.
     * @param retrievedWorkflows The workflows to search through.
     * @param workflowsToFind The workflows to search for.
     */
    private void checkWorkflowsReturned(ExistingWorkflows retrievedWorkflows, List<ExistingWorkflow> workflowsToFind){
        for(ExistingWorkflow retrievedWorkflow: retrievedWorkflows.getWorkflows()){
            Optional<ExistingWorkflow> foundWorkflow = workflowsToFind.stream()
                    .filter(filterWorkflow -> filterWorkflow.getId().equals(retrievedWorkflow.getId())).findFirst();
            if(foundWorkflow.isPresent()) {
                compareWorkflows(foundWorkflow.get(), retrievedWorkflow);
                //remove from the list of workflows for next check
                workflowsToFind.remove(foundWorkflow.get());
            }
        }
    }

    /**
     * Creates a BaseWorkflow object, initializes its properties and returns the object.
     * @return The constructed BaseWorkflow object.
     */
    private BaseWorkflow intializeWorkflow()
    {
        BaseWorkflow workflow = new BaseWorkflow();
        workflow.setName("name_"+UUID.randomUUID().toString());
        workflow.setDescription("description_"+UUID.randomUUID().toString());
        workflow.setNotes("notes_"+UUID.randomUUID().toString());
        return workflow;
    }

    private void compareWorkflows(BaseWorkflow expectedWorkflow, ExistingWorkflow returnedWorkflow){
        Assert.assertEquals(returnedWorkflow.getName(), expectedWorkflow.getName(),
                "Expected name on workflows to match.");
        Assert.assertEquals(returnedWorkflow.getDescription(), expectedWorkflow.getDescription(),
                "Expected description on workflows to match");
        Assert.assertEquals(returnedWorkflow.getNotes(), expectedWorkflow.getNotes(),
                "Expected notes on workflows to match.");
    }
}
