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
package com.github.cafdataprocessing.utilities.initialization.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.client.model.BaseWorkflow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JSON representation of a data processing workflow for use with task submitter application
 */
public class WorkflowJson {
    private static final ObjectMapper mapper = new ObjectMapper();

    public final String name;
    public final String description;
    public final String notes;
    public final List<ProcessingRuleJson> processingRules;

    public WorkflowJson(@JsonProperty(value= "name", required = true)String name,
                        @JsonProperty(value= "description")String description,
                        @JsonProperty(value= "notes")String notes,
                        @JsonProperty(value= "processingRules")List<ProcessingRuleJson> processingRules){
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.processingRules = processingRules == null ? new ArrayList<>() : processingRules;
    }

    public BaseWorkflow toApiBaseWorkflow(){
        BaseWorkflow workflow = new BaseWorkflow();
        workflow.setName(this.name);
        workflow.setDescription(this.description);
        workflow.setNotes(this.notes);
        return workflow;
    }

    /**
     * Reads the contents of the file at the specified location and converts them to a WorkflowJson representation
     * @param inputFilepath Path to the file with WorkflowJson definition.
     * @return Converted WorkflowJson object.
     * @throws IOException If unable to convert file contents.
     * @throws NullPointerException
     *          If the <code>inputFilepath</code> argument is <code>null</code>
     */
    public static WorkflowJson readInputFile(String inputFilepath) throws IOException, NullPointerException {
        File baseDataFile = new File(inputFilepath);
        try {
            return mapper.readValue(baseDataFile, WorkflowJson.class);
        } catch (IOException e) {
            throw new IOException("Failure trying to deserialize the workflow base data input file. Please check the format of the file contents.", e);
        }
    }

    /**
     * Reads the the input stream and converts to a WorkflowJson representation
     * @param inputStream InputStream of a WorkflowJson definition.
     * @return Converted WorkflowJson object.
     * @throws IOException If unable to convert file contents.
     * @throws NullPointerException
     *          If the <code>inputStream</code> argument is <code>null</code>
     */
    public static WorkflowJson readInputStream(InputStream inputStream) throws IOException, NullPointerException
    {
        Objects.requireNonNull(inputStream);
        try {
            return mapper.readValue(inputStream, WorkflowJson.class);
        } catch (IOException e) {
            throw new IOException("Failure trying to deserialize the workflow base data input stream. Please check the format of the input stream.", e);
        }
    }
}
