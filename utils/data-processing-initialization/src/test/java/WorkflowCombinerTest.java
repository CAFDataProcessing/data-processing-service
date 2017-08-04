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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.utilities.initialization.WorkflowCombiner;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ActionJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ProcessingRuleJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.WorkflowJson;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class WorkflowCombinerTest
{
    @Test
    public void combineTest() throws Exception
    {
        InputStream workflowInputFileStream = WorkflowCombinerTest.class.getResourceAsStream("/processing-workflow.json");
        WorkflowJson workflowJson = readInputFile(workflowInputFileStream);
        System.out.println(workflowJson.name);
        WorkflowJson overlayWorkflow = readInputFile(getResourceAsStream("/processing-workflow-overlay.json"));
        WorkflowCombiner.combineWorkflows(workflowJson, overlayWorkflow);

        ProcessingRuleJson metadataProcessingRule = getProcessingRule(workflowJson.processingRules, "Metadata Processing");
        Assert.assertEquals(13, metadataProcessingRule.actions.size());

        ActionJson langDetect = getAction(metadataProcessingRule.actions, "LangDetect");
        Assert.assertEquals(langDetect.order, (Integer) 440);
        Assert.assertEquals(langDetect.typeName, "DocumentWorkerHandler");
        Assert.assertEquals(langDetect.settings.size(), 2);
        Assert.assertEquals(langDetect.settings.get("workerName"), "langdetectworker");
        Assert.assertNotNull(langDetect.settings.get("fields"));
        Assert.assertEquals(langDetect.actionConditions.size(), 1);
        Assert.assertEquals(langDetect.actionConditions.get(0).name, "CONTENTexists");
        Assert.assertNotNull(langDetect.actionConditions.get(0).additional);

        ActionJson docIdWorker = getAction(metadataProcessingRule.actions, "DocIdWorker");
        Assert.assertEquals(docIdWorker.order, (Integer) 900);
        Assert.assertEquals(docIdWorker.typeName, "DocId");
        Assert.assertEquals(docIdWorker.settings.size(), 2);
        Assert.assertEquals(docIdWorker.settings.get("mySettingA"), "abc");
        Assert.assertEquals(docIdWorker.settings.get("mySettingB"), true);
        Assert.assertEquals(docIdWorker.actionConditions.size(), 1);

        Assert.assertEquals(docIdWorker.actionConditions.get(0).name, "CONTENTexists");
        Assert.assertNotNull(docIdWorker.actionConditions.get(0).additional);

        ProcessingRuleJson outputProcessingRule = getProcessingRule(workflowJson.processingRules, "Output");
        Assert.assertEquals(outputProcessingRule.actions.size(), 2);
        ActionJson mappingAction = getAction(outputProcessingRule.actions, "Field Mapping StorageReference");
        Assert.assertEquals(mappingAction.settings.size(), 1);
        Map mappings = (Map) mappingAction.settings.get("mappings");
        Assert.assertEquals(mappings.get("storageReference"), "ARCHIVE_ID");
        Assert.assertEquals(mappings.get("anotherMapping"), "ANOTHER_MAPPING");

        ActionJson outputAction = getAction(outputProcessingRule.actions, "Send to Output Queue");
        Assert.assertEquals(outputAction.typeName, "GenericQueueHandler");
        Assert.assertEquals(outputAction.settings.size(), 1);
        Assert.assertEquals(outputAction.settings.get("queueName"), "relocator");
    }

    private ProcessingRuleJson getProcessingRule(List<ProcessingRuleJson> processingRules, String name)
    {
        return processingRules.stream().filter(r -> r.name.equals(name)).findFirst().get();
    }

    private ActionJson getAction(List<ActionJson> actions, String name)
    {
        return actions.stream().filter(a -> a.name.equals(name)).findFirst().get();
    }

    private InputStream getResourceAsStream(String name)
    {
        return getClass().getResourceAsStream(name);
    }

    private WorkflowJson readInputFile(InputStream baseDataFile) throws IOException
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(baseDataFile, WorkflowJson.class);
        } catch (IOException e) {
            throw new IOException("Failure trying to deserialize the workflow base data input file. Please check the format of the file contents.", e);
        }
    }
}
