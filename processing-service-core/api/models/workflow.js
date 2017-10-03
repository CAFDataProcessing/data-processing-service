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
//this represents the data processing service Workflow and operations on that type.
var Q = require('q');
var policyWorkflowModel = require('./policy_api/workflow.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var logger = require('../helpers/loggingHelper.js');

module.exports.create = function(createWorkflowParams){
  var createPolicyWorkflowParams = {
    description: createWorkflowParams.description,
    name: createWorkflowParams.name,
    notes: createWorkflowParams.notes
  };
  var deferredCreateWorkflow = Q.defer();
  policyWorkflowModel.create(createWorkflowParams.project_id, createPolicyWorkflowParams)
  .then(function(createdWorkflow){
    logger.debug("Created Policy API workflow with ID: "+createdWorkflow.id);
    var builtWorkflow = policyToProcessingHelper.buildWorkflowFromPolicyWorkflow(createdWorkflow);
    deferredCreateWorkflow.resolve(builtWorkflow);
  })
  .fail(function(errorResponse){
    deferredCreateWorkflow.reject(errorResponse);
  }).done();
  return deferredCreateWorkflow.promise;
};
module.exports.get = function(getWorkflowParams){
  var deferredGetWorkflow = Q.defer();
  policyWorkflowModel.validateWorkflowExists(getWorkflowParams.project_id, getWorkflowParams.id)
  .then(function(retrievedPolWorkflow){
    logger.debug("Retrieved workflow from Policy API with ID: "+getWorkflowParams.id);
    var builtWorkflow = policyToProcessingHelper.buildWorkflowFromPolicyWorkflow(retrievedPolWorkflow);
    deferredGetWorkflow.resolve(builtWorkflow);
  })
  .fail(function(errorResponse){
    deferredGetWorkflow.reject(errorResponse);
  }).done();
  return deferredGetWorkflow.promise;
};

module.exports.getWorkflows = function(getWorkflowsParams){
  var deferredGetWorkflows = Q.defer();
  
  policyWorkflowModel.getWorkflows(getWorkflowsParams.project_id, getWorkflowsParams.pageNum, 
    getWorkflowsParams.pageSize)
  .then(function(retrievedWorkflows){
    logger.debug("Retrieved workflows from Policy API");
    var builtWorkflows = [];
    for(var polWorkflow of retrievedWorkflows.results){
      builtWorkflows.push(policyToProcessingHelper.buildWorkflowFromPolicyWorkflow(polWorkflow));
    }
    deferredGetWorkflows.resolve({
      totalHits: retrievedWorkflows.totalhits,
      workflows: builtWorkflows
    });
  })
  .fail(function(errorResponse){
    deferredGetWorkflows.reject(errorResponse);
  }).done();
  
  return deferredGetWorkflows.promise;
};

module.exports.update = function(updateWorkflowParams){
  var deferredUpdateWorkflow = Q.defer();
  
  //in updating the Workflow we need to retrieve the sequence entries on the Policy Workflow to pass in our update call
  //otherwise the Policy API will consider this as us removing all Workflow entries.
  policyWorkflowModel.validateWorkflowExists(updateWorkflowParams.project_id, updateWorkflowParams.id)
  .then(function(retrievedWorkflow){
    logger.debug("Validated workflow exists before update. ID: "+updateWorkflowParams.id);
    //use the additional information retrieved to update the Workflow without removing the entries
    var updatePolWorkflowParams = {
      description: updateWorkflowParams.description,
      id: updateWorkflowParams.id,
      name: updateWorkflowParams.name,
      additional: {
        notes: updateWorkflowParams.notes,
        sequence_entries: retrievedWorkflow.additional.sequence_entries
      }
    };    
    return policyWorkflowModel.update(updateWorkflowParams.project_id, updatePolWorkflowParams);
  })
  .then(function(updatedPolWorkflow){
    deferredUpdateWorkflow.resolve(updatedPolWorkflow);
  })
  .fail(function(errorResponse){
    deferredUpdateWorkflow.reject(errorResponse);
  }).done();
  return deferredUpdateWorkflow.promise;
};

module.exports.delete = function(deleteWorkflowParams){
  var deferredDeleteWorkflow = Q.defer();
    
  policyWorkflowModel.validateWorkflowExists(deleteWorkflowParams.project_id, deleteWorkflowParams.id)
  .then(function(retrievedWorkflow){
    logger.debug("Validated workflow exists before delete. ID: "+deleteWorkflowParams.id);
    //having retrieved the workflow, check for any Collection Sequence Entries on it (Rules).
    if(retrievedWorkflow.additional.sequence_entries !== undefined && retrievedWorkflow.additional.sequence_entries.length > 0){
      //TODO current implementation is set to throw an Error when Workflow has Rules, rather than delete Rules and Actions, until CAF-1436 completed.
      deferredDeleteWorkflow.reject("Unable to Delete. There are Rules on the Workflow.");
      return Q({complete: true});
      //TODO update workflow to have no entries.
      /*var updatedWorkflow = {
        additional: {},
        id: deleteWorkflowParams.id    
      }
      policyWorkflowModel.removeAllSequencesFromWorkflowEntries()*/
    }
    return Q({});
  })
  .then(function(result){
    //handles case where logic flow has completed earlier in the sequence
    if(result.complete){
      return Q({complete: true});
    }
    //delete the Workflow
    return policyWorkflowModel.deleteWorkflowById(deleteWorkflowParams.project_id, deleteWorkflowParams.id);
  })
  .then(function(result){
    if(result.complete){
      return Q({complete: true});
    }
    deferredDeleteWorkflow.resolve(result);
  })
  .fail(function(errorResponse){
    deferredDeleteWorkflow.reject(errorResponse);
  }).done();

  return deferredDeleteWorkflow.promise;
};