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
    public final String description;
    public final Boolean enabled;
    public final Integer priority;
    public final List<ActionJson> actions;
    public final List<ConditionJson> ruleConditions;

    public ProcessingRuleJson(@JsonProperty(value= "name", required = true)String name,
                              @JsonProperty(value= "description")String description,
                              @JsonProperty(value= "enabled")Boolean enabled,
                              @JsonProperty(value= "priority")Integer priority,
                              @JsonProperty(value= "actions")List<ActionJson> actions,
                              @JsonProperty(value= "ruleConditions")List<ConditionJson> ruleConditions){
        this.name = name;
        this.description = description;
        this.enabled = enabled == null ? true : enabled;
        this.priority = priority;
        this.actions = actions;
        this.ruleConditions = ruleConditions;
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
