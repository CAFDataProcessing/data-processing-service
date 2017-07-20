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
import com.github.cafdataprocessing.processing.service.client.model.BaseProcessingRule;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions.ConditionJson;

import java.util.List;

/**
 * JSON representation of a data processing processing rule for use with task submitter application
 */
public class ProcessingRuleJson {
    public final String name;
    public String description;
    public Boolean enabled;
    public Integer priority;
    public List<ActionJson> actions;
    public List<ConditionJson> ruleConditions;
    public MergeMode mergeMode;

    public ProcessingRuleJson(@JsonProperty(value= "name", required = true)String name,
                              @JsonProperty(value= "description")String description,
                              @JsonProperty(value= "enabled")Boolean enabled,
                              @JsonProperty(value= "priority")Integer priority,
                              @JsonProperty(value= "actions")List<ActionJson> actions,
                              @JsonProperty(value= "ruleConditions")List<ConditionJson> ruleConditions,
                              @JsonProperty(value = "mergeMode") MergeMode mergeMode){
        this.name = name;
        this.description = description;
        this.enabled = enabled == null ? true : enabled;
        this.priority = priority;
        this.actions = actions;
        this.ruleConditions = ruleConditions;
        this.mergeMode = mergeMode == null ? MergeMode.MERGE : mergeMode;
    }

    public BaseProcessingRule toApiBaseProcessingRule(){
        BaseProcessingRule rule = new BaseProcessingRule();
        rule.setName(this.name);
        rule.setEnabled(this.enabled);
        rule.setDescription(this.description);
        rule.setPriority(priority);
        return rule;
    }
}
