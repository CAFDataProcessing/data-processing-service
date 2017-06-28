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


/**
 * JSON representation of a data processing number condition 'additional' property for use with task submitter application
 */
public class NumberConditionAdditionalJson extends ConditionAdditionalJson {
    public long value;
    public com.github.cafdataprocessing.processing.service.client.model.NumberConditionAdditional.OperatorEnum operator;
    public String field;
}
