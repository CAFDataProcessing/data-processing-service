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
//holds functions for manipulation of objects specific to the Processing API
var logger = require('./loggingHelper.js');

module.exports = {
  rules: {
    getRulePrioritiesFromPolicyWorkflow: getRulePrioritiesFromPolicyWorkflow
  }
};

//given a Policy Workflow object that has sequence_entries, returns a map of Rule IDs to priorities. Returns empty object if no entries on Workflow.
function getRulePrioritiesFromPolicyWorkflow(workflow){
  if(workflow===undefined || workflow===null || 
    workflow.additional===undefined || workflow.additional===null || 
    workflow.additional.sequence_entries===undefined || workflow.additional.sequence_entries===null ||
    workflow.additional.sequence_entries.length===0){
    return {};
  }
  var rulesMap = {};
  for(var entry of workflow.additional.sequence_entries){
    rulesMap[entry.collection_sequence_id] = entry.order;
  }
  return rulesMap;
}