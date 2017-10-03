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
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.WorkflowsApi;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflow;
import com.github.cafdataprocessing.processing.service.client.model.ExistingWorkflows;
import com.github.cafdataprocessing.utilities.initialization.ProcessingApisProvider;
import com.github.cafdataprocessing.utilities.initialization.WorkflowInitializationParams;
import com.github.cafdataprocessing.utilities.initialization.WorkflowInitializer;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.WorkflowJson;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Tests that workflow initialization class works occurs as expected.
 */
public class WorkflowInitializerIT {
    private final static ProcessingApisProvider apisProvider;
    private final static String processingServiceUrl;
    private final static String testWorkflowInputFilePath;

    static {
        processingServiceUrl = System.getenv("processingServiceUrl");
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(processingServiceUrl);
        apisProvider = new ProcessingApisProvider(apiClient);

        String testWorkflowInputFileLocation = Thread.currentThread().getContextClassLoader().getResource("overwrite-test-workflow.json").getFile();
        testWorkflowInputFilePath = new File(testWorkflowInputFileLocation).getAbsolutePath();
    }

    @Test(description = "Tests that a read in JSON definition is successfully created and that if create is called again "+
    "that the first set of entities created are removed and a new set created.")
    public void initializeTwiceOverwriteDefaultTest() throws IOException, ApiException {
        WorkflowInitializer initializer = WorkflowInitializer.createWorkflowOnlyInitializer(processingServiceUrl);
        String testProjectId = UUID.randomUUID().toString();
        final WorkflowJson expectedWorkflowDefinition = WorkflowJson.readInputFile(testWorkflowInputFilePath);

        Long firstCreatedWorkflowId = initializer.initializeWorkflowBaseData(testWorkflowInputFilePath, testProjectId);

        WorkflowsApi workflowsApi = apisProvider.getWorkflowsApi();
        ExistingWorkflow firstCreatedWorkflow = workflowsApi.getWorkflow(testProjectId, firstCreatedWorkflowId);
        Assert.assertEquals(firstCreatedWorkflow.getName(), expectedWorkflowDefinition.name,
                "First Workflow should have been created with expected name.");
        ExistingWorkflows firstCreatedWorkflows = workflowsApi.getWorkflows(testProjectId, 1, 100);
        Assert.assertEquals((int) firstCreatedWorkflows.getTotalHits(), 1,
                "Expecting only one workflow to exist after caling initialize for first time.");

        Long secondCreatedWorkflowId = initializer.initializeWorkflowBaseData(testWorkflowInputFilePath, testProjectId);
        Assert.assertNotEquals(secondCreatedWorkflowId, firstCreatedWorkflowId,
                "ID of second created workflow should not be the same as the first created workflow.");
        ExistingWorkflow secondCreatedWorkflow = workflowsApi.getWorkflow(testProjectId, secondCreatedWorkflowId);
        Assert.assertEquals(secondCreatedWorkflow.getName(), expectedWorkflowDefinition.name,
                "Second workflow should have been created with expected name.");

        ExistingWorkflows secondRetrievedWorkflows = workflowsApi.getWorkflows(testProjectId, 1, 100);
        Assert.assertEquals((int) secondRetrievedWorkflows.getTotalHits(), 1,
                "Expecting workflow retrieval after second create to only return a single worfklow.");
        ExistingWorkflow remainingWorkflow = secondRetrievedWorkflows.getWorkflows().get(0);
        Assert.assertEquals(remainingWorkflow.getId(), secondCreatedWorkflowId,
                "The curent existing workflow should have the ID of the second created workflow.");
    }

    @Test(description = "Tests that a read in JSON creation definition is successfully created and that if create is called again "+
            " that the first set of entities created are not removed and a new set created.")
    public void initializeTwiceOverwriteFalseTest() throws IOException, ApiException {
        WorkflowInitializer initializer = WorkflowInitializer.createWorkflowOnlyInitializer(processingServiceUrl);
        String testProjectId = UUID.randomUUID().toString();
        final WorkflowJson expectedWorkflowDefinition = WorkflowJson.readInputFile(testWorkflowInputFilePath);
        Long firstCreatedWorkflowId = initializer.initializeWorkflowBaseData(testWorkflowInputFilePath, testProjectId);

        WorkflowsApi workflowsApi = apisProvider.getWorkflowsApi();
        ExistingWorkflow firstCreatedWorkflow = workflowsApi.getWorkflow(testProjectId, firstCreatedWorkflowId);
        Assert.assertEquals(firstCreatedWorkflow.getName(), expectedWorkflowDefinition.name,
                "First Workflow should have been created with expected name.");
        ExistingWorkflows firstCreatedWorkflows = workflowsApi.getWorkflows(testProjectId, 1, 100);
        Assert.assertEquals((int) firstCreatedWorkflows.getTotalHits(), 1,
                "Expecting only one workflow to exist after caling initialize for first time.");


        WorkflowInitializationParams initializationParams = new WorkflowInitializationParams(testWorkflowInputFilePath,
                testProjectId);
        initializationParams.setOverwriteExisting(false);
        Long secondCreatedWorkflowId = initializer.initializeWorkflowBaseData(initializationParams);
        Assert.assertNotEquals(secondCreatedWorkflowId, firstCreatedWorkflowId,
                "ID of second created workflow should not be the same as the first created workflow.");
        ExistingWorkflow secondCreatedWorkflow = workflowsApi.getWorkflow(testProjectId, secondCreatedWorkflowId);
        Assert.assertEquals(secondCreatedWorkflow.getName(), expectedWorkflowDefinition.name,
                "Second workflow should have been created with expected name.");

        ExistingWorkflows secondRetrievedWorkflows = workflowsApi.getWorkflows(testProjectId, 1, 100);
        Assert.assertEquals((int) secondRetrievedWorkflows.getTotalHits(), 2,
                "Expecting workflow retrieval after second create to return both workflows.");
        List<Long> expectedIds = new ArrayList<>();
        expectedIds.add(firstCreatedWorkflowId);
        expectedIds.add(secondCreatedWorkflowId);
        Assert.assertTrue(secondRetrievedWorkflows.getWorkflows().stream()
                        .map(wf -> wf.getId()).collect(Collectors.toList())
                        .containsAll(expectedIds),
                "Expecting both workflow IDs to have been returned in retrieve call.");
    }
}
