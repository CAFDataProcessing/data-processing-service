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
package com.github.cafdataprocessing.utilities.tasksubmitter.initialize.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cafdataprocessing.processing.service.client.model.BaseWorkflow;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON representation of a data processing workflow for use with task submitter application
 */
public class WorkflowJson {
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
}
