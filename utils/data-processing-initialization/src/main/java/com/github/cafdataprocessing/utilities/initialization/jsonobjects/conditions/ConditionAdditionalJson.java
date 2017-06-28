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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class of common condition properties
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanConditionAdditionalJson.class, name = "boolean"),
        @JsonSubTypes.Type(value = DateConditionAdditionalJson.class, name = "date"),
        @JsonSubTypes.Type(value = ExistsConditionAdditionalJson.class, name = "exists"),
        @JsonSubTypes.Type(value = NotConditionAdditionalJson.class, name = "not"),
        @JsonSubTypes.Type(value = NumberConditionAdditionalJson.class, name = "number"),
        @JsonSubTypes.Type(value = RegexConditionAdditionalJson.class, name = "regex"),
        @JsonSubTypes.Type(value = StringConditionAdditionalJson.class, name = "string")
})
public class ConditionAdditionalJson {
    public String type;
    public Integer order;
    public String notes;
}
