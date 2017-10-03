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
package com.github.cafdataprocessing.utilities.initialization.jsonobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.cafdataprocessing.processing.service.client.model.Action;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions.ConditionJson;
import com.google.common.base.Strings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON representation of a data processing action for use with task submitter application
 */
public class ActionJson {
    public String name;
    public String description;
    public Integer order;
    public LinkedHashMap<String, Object> settings;
    public List<ConditionJson> actionConditions;
    public MergeMode mergeMode;

    //The name of the type will be used to retrieve the actual action type ID if set
    public String typeName;

    public Long typeId;

    public ActionJson(@JsonProperty(value= "name", required = true)String name,
                      @JsonProperty(value= "description")String description,
                      @JsonProperty(value= "order")Integer order,
                      @JsonProperty(value= "settings")LinkedHashMap<String, Object> settings,
                      @JsonProperty(value= "actionConditions")List<ConditionJson> actionConditions,
                      @JsonProperty(value= "typeName")String typeName,
                      @JsonProperty(value= "typeId")Long typeId,
                      @JsonProperty(value = "mergeMode") MergeMode mergeMode){
        this.name = name;
        this.description = description;
        this.order = order;
        this.settings = settings == null ? new LinkedHashMap<>() : settings;
        this.actionConditions = actionConditions;
        this.typeId = typeId;
        this.typeName = typeName;

        this.mergeMode = mergeMode == null ? MergeMode.MERGE : mergeMode;
    }

    public Action toApiAction(Map<String, Long> typeNamesToIds){
        Action action = new Action();
        action.setName(this.name);
        action.setDescription(this.description);
        action.setOrder(this.order);
        action.setSettings(this.settings);
        if(typeId!=null){
            action.setTypeId(typeId);
            return action;
        }
        //no typeID passed, try to find the matching type ID for a specified type name
        if(typeNamesToIds !=null && !typeNamesToIds.containsKey(this.typeName)){
            action.setTypeId(null);
        }
        else{
            action.setTypeId(typeNamesToIds.get(this.typeName));
        }
        return action;
    }
}
