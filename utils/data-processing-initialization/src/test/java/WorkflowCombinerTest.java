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
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
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
        assertEquals(13, metadataProcessingRule.actions.size());

        ActionJson langDetect = getAction(metadataProcessingRule.actions, "LangDetect");
        assertEquals((Integer)440, langDetect.order);
        assertEquals("DocumentWorkerHandler", langDetect.typeName);
        assertEquals(2, langDetect.settings.size());
        assertEquals("langdetectworker", langDetect.settings.get("workerName"));
        assertNotNull(langDetect.settings.get("fields"));
        assertEquals(1, langDetect.actionConditions.size());
        assertEquals("CONTENTexists", langDetect.actionConditions.get(0).name);
        assertNotNull(langDetect.actionConditions.get(0).additional);

        ActionJson docIdWorker = getAction(metadataProcessingRule.actions, "DocIdWorker");
        assertEquals((Integer)900, docIdWorker.order);
        assertEquals("DocId", docIdWorker.typeName);
        assertEquals(2, docIdWorker.settings.size());
        assertEquals("abc", docIdWorker.settings.get("mySettingA"));
        assertEquals(true, docIdWorker.settings.get("mySettingB"));
        assertEquals(1, docIdWorker.actionConditions.size());

        assertEquals("CONTENTexists", docIdWorker.actionConditions.get(0).name);
        assertNotNull(docIdWorker.actionConditions.get(0).additional);

        ProcessingRuleJson outputProcessingRule = getProcessingRule(workflowJson.processingRules, "Output");
        assertEquals(2, outputProcessingRule.actions.size());
        ActionJson mappingAction = getAction(outputProcessingRule.actions, "Field Mapping StorageReference");
        assertEquals(1, mappingAction.settings.size());
        Map mappings = (Map) mappingAction.settings.get("mappings");
        assertEquals("ARCHIVE_ID", mappings.get("storageReference"));
        assertEquals("ANOTHER_MAPPING", mappings.get("anotherMapping"));

        ActionJson outputAction = getAction(outputProcessingRule.actions, "Send to Output Queue");
        assertEquals("GenericQueueHandler", outputAction.typeName);
        assertEquals(1, outputAction.settings.size());
        assertEquals("relocator", outputAction.settings.get("queueName"));

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
