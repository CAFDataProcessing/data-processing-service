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
var Q = require('q');
var logger = require('../../helpers/loggingHelper.js');
var policyApiHelper = require('../../helpers/policyApiHelpers.js');
var promiseHelper = require('../../helpers/httpPromiseHelper.js');
var pagingHelper = require('../../helpers/pagingHelper.js');
var apiErrorFactory = require('../errors/apiErrorFactory.js');

module.exports = {
  create: create,
  deleteWorkflowById: deleteWorkflowById,
  getWorkflowEntries: getWorkflowEntries,
  getWorkflowEntriesByWorkflowId: getWorkflowEntriesByWorkflowId,
  getWorkflowEntriesByWorkflowIdAndCollectionSequenceId: getWorkflowEntriesByWorkflowIdAndCollectionSequenceId,
  getWorkflows: getWorkflows,
  getWorkflowById: getWorkflowById,
  getWorkflowsWithCollectionSequenceId: getWorkflowsWithCollectionSequenceId,
  insertCollectionSequenceIntoWorkflowEntries: insertCollectionSequenceIntoWorkflowEntries,
  removeAllSequencesFromWorkflowEntries: removeAllSequencesFromWorkflowEntries,
  removeCollectionSequenceFromWorkflowEntries: removeCollectionSequenceFromWorkflowEntries,
  update: update,
  updateOrderOnWorkflowEntries: updateOrderOnWorkflowEntries,
  updateWorkflow: updateWorkflow,
  validateWorkflowExists: validateWorkflowExists
};

//returns a params object with common parameters for a Workflow. Takes in a project ID and uses that in the params.
var getDefaultWorkflowParams = function(projectId){
  return {
    project_id: projectId,
    type: "sequence_workflow"
  };
};

//returns a params object with common parameters for a Workflow entry. Takes in a project ID and uses that in the params.
var getDefaultWorkflowEntryParams = function(projectId){
  return {
    project_id: projectId,
    type: "sequence_workflow_entry"
  };
};

//Creates a Workflow using the specified definition object passed. Returns a promise that resolves with the created Workflow.
function create(project_id, newWorkflow){
  var createParams = getDefaultWorkflowParams(project_id);
  createParams.description = newWorkflow.description;
  createParams.notes = newWorkflow.notes;
  createParams.name = newWorkflow.name;
  
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("workflow/create", createParams);
}

//Updates a Workflow using the specified definition object passed. Returns a promise that resolves with the updated Workflow.
function update(project_id, updatedWorkflow){
  var updateParams = getDefaultWorkflowParams(project_id);
  updateParams.additional = updatedWorkflow.additional;
  updateParams.description = updatedWorkflow.description;
  updateParams.id = updatedWorkflow.id;
  updateParams.name = updatedWorkflow.name;
  updateParams.notes = updatedWorkflow.notes;
    
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("workflow/update", updateParams);
}

//Deletes a Workflow with the specified ID. Returns a promise.
function deleteWorkflowById(project_id, workflowId){
  var deleteParams = getDefaultWorkflowParams(project_id);
  deleteParams.id = workflowId;
  
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("workflow/delete", deleteParams);
}

//Returns a promise to retrieve a Workflow by it's ID. Returns a promise.
function getWorkflowById(project_id, workflowId){
  var deferredGet = Q.defer();
  var getParams = getDefaultWorkflowParams(project_id);
  getParams.id = workflowId;
  
  //workflow retrieve returns an array of results when requesting by ID so we need to extract the first result
  var workflowExtract = function(workflowRetrieveResult){
    if(workflowRetrieveResult === null || workflowRetrieveResult === undefined || 
      workflowRetrieveResult.totalhits === 0 || 
      workflowRetrieveResult.results === null || workflowRetrieveResult.results === undefined ){
      //the API should complain and throw an error if Workflow not found but we will handle it just in case.
      throw new Error('No match returned for the Workflow requested. ID: '+getWorkflowParams.workflowId);
    }
    if(workflowRetrieveResult.totalhits > 1){
      logger.logWarn('More than one result returned when retrieving a Workflow by ID: '+ getWorkflowParams.workflowId);
    }
    return workflowRetrieveResult.results[0];
  };
  
  policyApiHelper.HttpHelper.policyAPIGetRequest("workflow/retrieve", getParams, 
    promiseHelper.handlePotentialSuccess(deferredGet, workflowExtract), promiseHelper.handleFailure(deferredGet));
  return deferredGet.promise;
}

function getWorkflows(project_id, pageNum, pageSize){
  var pageOptions = pagingHelper.getValidatedPagingParams(pageNum, pageSize);
  var getWorkflowsParams = getDefaultWorkflowParams(project_id);
  getWorkflowsParams.max_page_results = pageOptions.pageSize;
  getWorkflowsParams.start = pageOptions.start;
    
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemsRequest("workflow/retrieve", getWorkflowsParams);
}

//Returns a promse to retrieve all Workflows with entries for the specified Collection Sequence ID
function getWorkflowsWithCollectionSequenceId(project_id, collectionSequenceId){
  var deferredGet = Q.defer();
  var getParams = getDefaultWorkflowParams(project_id);
  getParams.additional = {
    filter: {
      'sequence_entries.collection_sequence_id': collectionSequenceId
    }
  };
  //TODO this is just a high number to avoid paging cutting off entries, would be better to perform page request mutliple times until we reach total number of results. For simplicity of implementation today using this max results value in the interim.
  getParams.max_page_results = 2147483647;
  
  
  policyApiHelper.HttpHelper.policyAPIGetRequest("workflow/retrieve", getParams, 
    promiseHelper.handlePotentialSuccess(deferredGet), promiseHelper.handleFailure(deferredGet)
  );
  return deferredGet.promise;
}

//Returns a promise to retrieve Workflow Entries for a specified Workflow ID. Default number of entries returned is 100
//project_id  - project_id to use in retrieving entries
//workflowId  - Workflow ID that entries must be under
//pageNum     - Optional. The page number to return results from. Defaults to 1.
//pageSize    - Optional. The max number of entries to return. Defaults to 100.
function getWorkflowEntriesByWorkflowId(project_id, workflowId, pageNum, pageSize){
  var additional = {
    filter: {
      sequence_workflow_id: workflowId
    }
  };
  return getWorkflowEntries(project_id, additional, pageNum, pageSize);
}

//Returns a promise to retrieve workflow entries for a particular Workflow ID and Collection Sequence ID. Default number of entries returned is 100.
function getWorkflowEntriesByWorkflowIdAndCollectionSequenceId(project_id, workflowId, colSeqId, pageNum, pageSize){
  var additional = {
    filter: {
      "sequence_workflow_id": workflowId,
      "collection_sequence.id": colSeqId
    }
  };  
  
  return getWorkflowEntries(project_id, additional, pageNum, pageSize);
}

//Returns a promise to retrieve workflow entries. object to set for 'additional' property may be passed as second argument. Default number of entries returned is 100.
function getWorkflowEntries(project_id, additional, pageNum, pageSize){
  var pageOptions = pagingHelper.getValidatedPagingParams(pageNum, pageSize);
  var getWorkflowEntriesParams = getDefaultWorkflowEntryParams(project_id);
  if(additional!==null && additional!==undefined){
    getWorkflowEntriesParams.additional = additional;
  }
  getWorkflowEntriesParams.max_page_results = pageOptions.pageSize;
  getWorkflowEntriesParams.start = pageOptions.start;

  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemsRequest("workflow/retrieve", getWorkflowEntriesParams);
}

//updates a Workflow. Returns a promise.
function updateWorkflow(project_id, updatedWorkflow){
  var deferredUpdate = Q.defer();
  var updateParams = getDefaultWorkflowParams(project_id);
  updateParams.additional = updatedWorkflow.additional;
  updateParams.description = updatedWorkflow.description;
  updateParams.id = updatedWorkflow.id;
  updateParams.name = updatedWorkflow.name;  
  
  //call API to add collection sequence to workflow.
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("workflow/update", updateParams);
}

//adds an entry to the specified workflow for the given collection sequence ID. Returns the inserted entry.
//workflow - the workflow object to update with the new entry
//collectionSequenceId - Id of the collection sequence to add as an entry
//order - Optional. The order to set on the entry. If none provided then the order will be set to the highest order of the entries on the Workflow + 1.
function insertCollectionSequenceIntoWorkflowEntries(workflow, collectionSequenceId, order){
  var newEntry = {
    collection_sequence_id: collectionSequenceId,
    sequence_workflow_id: workflow.id
  };
  
  //if an order was passed we set entry to use that order and increment existing entries greater than that order by 1, otherwise set the order to the highest order + 1
  if(order!==null && order!==undefined){
    newEntry.order = order;
    for(var entry of workflow.additional.sequence_entries){
      if(entry.order >= order){
        entry.order++;
      }
    }
  }
  else{
    //find the current highest order
    var highestOrder = 0;
    for(var entry of workflow.additional.sequence_entries){
      if(entry.order > highestOrder){
        highestOrder = entry.order;
      }
    }
    newEntry.order = highestOrder + 1;
  }
  //add to the list of entries
  workflow.additional.sequence_entries.push(newEntry);
  return newEntry;
}

//removes an entry from specified workflow object matching given collection sequence ID. Returns the removed entry or null if no matching entry found.
//workflow - the workflow object to update.
//collectionSequenceId - the Id of the collection sequence on the entry to be removed.
function removeCollectionSequenceFromWorkflowEntries(workflow, collectionSequenceId){
  //find the entry
  
  var removedEntry = null;
  for(var index =0; index < workflow.additional.sequence_entries.length; index++){
    if(workflow.additional.sequence_entries[index].collection_sequence_id===collectionSequenceId){
      //remove the entry at this position from the entries array
      removedEntry = workflow.additional.sequence_entries.splice(index, 1);
      break;
    }
  }
  return removedEntry;
}

//updates the 'order' property of the entry on the workflow object passed, identified by the collection sequence ID argument passed.
function updateOrderOnWorkflowEntries(workflow, colSeqId, newOrder){
  for(var entry of workflow.additional.sequence_entries){
    if(entry.collection_sequence_id===colSeqId){
      entry.order = newOrder;
    }
  }
}

//removes all entries from a workflow object.
function removeAllSequencesFromWorkflowEntries(workflow){
  workflow.additional.sequence_entries = [];
}

//Retuns a promise to check that a given Policy Workflow exists. Resolved result will be the retrieved Policy Workflow.
function validateWorkflowExists(projectId, workflowId){
  var validatedWorkflowPromise = Q.defer();
  getWorkflowById(projectId, workflowId)
  .then(function(retrievedWorkflow){
    validatedWorkflowPromise.resolve(retrievedWorkflow);
  })
  .fail(function(errorResponse){
    //throwing a more helpful error here if indication is that the Workflow ID was wrong.
    if(errorResponse.response && errorResponse.response.reason === "Could not find a match for the SequenceWorkflow requested."){
      validatedWorkflowPromise.reject(apiErrorFactory.createNotFoundError("Unable to find Workflow with ID: "+workflowId));
    }
    else{
      validatedWorkflowPromise.reject(errorResponse);
    }
  }).done();
  return validatedWorkflowPromise.promise;
}