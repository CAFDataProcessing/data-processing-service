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
package com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cafdataprocessing.processing.service.client.model.Condition;

/**
 * JSON representation of a data processing condition for use with task submitter application
 */
public class ConditionJson {
    public final String name;
    public final ConditionAdditionalJson additional;

    public ConditionJson(@JsonProperty(value= "name")String name,
                         @JsonProperty(value= "additional", required = true)ConditionAdditionalJson additional){
        this.name = name;
        this.additional = additional;
    }

    public Condition toApiCondition(){
        Condition condition = new Condition();
        condition.setName(this.name);
        condition.setAdditional(this.additional);
        return condition;
    }
}
