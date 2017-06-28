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
    public final String name;
    public final String description;
    public final Integer order;
    public final LinkedHashMap<String, Object> settings;
    public final List<ConditionJson> actionConditions;

    //The name of the type will be used to retrieve the actual action type ID if set
    public String typeName;

    public Long typeId;

    public ActionJson(@JsonProperty(value= "name", required = true)String name,
                      @JsonProperty(value= "description")String description,
                      @JsonProperty(value= "order")Integer order,
                      @JsonProperty(value= "settings")LinkedHashMap<String, Object> settings,
                      @JsonProperty(value= "actionConditions")List<ConditionJson> actionConditions,
                      @JsonProperty(value= "typeName")String typeName,
                      @JsonProperty(value= "typeId")Long typeId){
        this.name = name;
        this.description = description;
        this.order = order;
        this.settings = settings == null ? new LinkedHashMap<>() : settings;
        this.actionConditions = actionConditions;

        if(typeId==null && Strings.isNullOrEmpty(typeName)){
            throw new RuntimeException(new JsonMappingException("'typeId' or 'typeName' property must be set on action. Neither currently set on action with name: "+name));
        }
        this.typeId = typeId;
        this.typeName = typeName;
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
