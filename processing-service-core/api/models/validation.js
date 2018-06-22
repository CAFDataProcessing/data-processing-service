/*
 * Copyright 2017-2018 Micro Focus or one of its affiliates.
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
//holds validation methods that may be used by multiple other Processing API level models
var Q = require('q');
var logger = require('../helpers/loggingHelper.js');
//MODELS
var collectionModel = require('./policy_api/collection.js');
var colSeqModel = require('./policy_api/collectionSequence.js');
var conditionModel = require('./policy_api/condition.js');
var workflowModel = require('./policy_api/workflow.js');
//HELPERS
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var ruleObjectsHelper = require('../helpers/processingObjectsHelper.js').rules;
//ERRORS
var apiErrorFactory = require('./errors/apiErrorFactory.js');

module.exports = {
  validateWorkflowAndSeqForRule: validateWorkflowAndSeqForRule,
  validateWorkflowSeqAndAction: validateWorkflowSeqAndAction,
  validateWorkflowSeqAndActionForCondition: validateWorkflowSeqAndActionForCondition
};
var defaultNoMatchMessage = "Could not retrieve Processing Rule with ID: ";

//Searches for a specified Col Seq on a specified Workflow and returns the Col Seq, Workflow and Processing Rule representation
function validateWorkflowAndSeqForRule(getParams){
  var deferredGet = Q.defer();  
  var retrievedProcessingRule;
  var associatedWorkflow;
  var rulePriority;
  
  workflowModel.validateWorkflowExists(getParams.project_id, getParams.workflowId)
  .then(function(retrievedWorkflow){
    logger.debug(function(){return "Retrieved Workflow with ID: "+getParams.workflowId+" when retrieving Processing Rule ID: "+getParams.id;});
    associatedWorkflow = retrievedWorkflow;
    
    //get priority from the Workflow result (sequence_entries should be on the Workflow)
    var rulesMap = ruleObjectsHelper.getRulePrioritiesFromPolicyWorkflow(retrievedWorkflow);
    rulePriority = rulesMap[getParams.id];
    if(rulePriority===undefined){      
      throw apiErrorFactory.createNotFoundError("Processing Rule ID: "+ getParams.id + " not found on Workflow with ID: "+getParams.workflowId);
    }    
    
    return colSeqModel.validateSequenceExists(getParams.project_id, getParams.id, false, defaultNoMatchMessage + getParams.id);
  })
  .then(function(retrievedColSeq){    
    retrievedProcessingRule = policyToProcessingHelper.buildRuleFromCollectionSequenceAndOrder(retrievedColSeq, rulePriority);
    deferredGet.resolve({
      processingRule: retrievedProcessingRule,
      collectionSequence: retrievedColSeq,
      workflow: associatedWorkflow
    });
  })
  .fail(function(errorResponse){
    deferredGet.reject(errorResponse);
  }).done();
  
  return deferredGet.promise;
}

//
function validateWorkflowSeqAndAction(validateParams, retrieveConditionsOnAction){
  var deferredValidate = Q.defer();  
  
  var validateWorkflowAndSequenceParams = {
    id: validateParams.ruleId,
    project_id: validateParams.project_id,
    workflowId: validateParams.workflowId
  };
  var validateResult = {};
  validateWorkflowAndSeqForRule(validateWorkflowAndSequenceParams)
  .then(function(validateWorkflowAndSeqResult){
    validateResult.collectionSequence = validateWorkflowAndSeqResult.collectionSequence;
    validateResult.processingRule = validateWorkflowAndSeqResult.processingRule;
    validateResult.workflow = validateWorkflowAndSeqResult.workflow;
    
    var retrievedSeq = validateResult.collectionSequence;
    //retrieve the collection entry on the sequence based on Rule ID passed, then retrieve the collection based on the ID of the action requested, then retrieve the Policy on that Collection (note Action only supports a single Policy)    
    if(retrievedSeq.additional.collection_sequence_entries.length===0){
      logger.debug("No collection entries on sequence with ID: "+validateParams.ruleId);
      throw apiErrorFactory.createNotFoundError("There are no Actions on Rule with ID: "+validateParams.ruleId);      
    }

    //iterate over collection entries to find the match for the ID requested
    var foundAction = {};
    for(var collectionEntry of retrievedSeq.additional.collection_sequence_entries){
      if(collectionEntry.collection_ids[0]!==validateParams.actionId){
        continue;
      }
      policyToProcessingHelper.buildActionFromCollectionEntry(collectionEntry, foundAction);
      break;
    }
    if(foundAction.id===undefined){
      logger.debug("Unable to find a collection entry on Collection Sequence ID: "+validateParams.ruleId +
        " with Collection ID: "+validateParams.actionId);
        throw apiErrorFactory.createNotFoundError("Unable to retrieve Action. No matching Action with ID: "+validateParams.actionId+
          " found on Rule ID: "+validateParams.ruleId);
    }
    
    //specify to include children when validating collection so it will return the child conditions, can use this to check the condition is on this Action
    return collectionModel.validateCollectionExists(validateParams.project_id,
      validateParams.actionId, retrieveConditionsOnAction === undefined || retrieveConditionsOnAction === null ? false : retrieveConditionsOnAction, "Unable to find Action with ID: "+validateParams.actionId);
  })
  .then(function(retrievedCollection){
    validateResult.collection = retrievedCollection;
    deferredValidate.resolve(validateResult);
  })
  .fail(function(errorResponse){
    deferredValidate.reject(errorResponse);
  }).done();
  
  return deferredValidate.promise;
}

function validateWorkflowSeqAndActionForCondition(validateParams){
  var deferredValidate = Q.defer();  
  
  var validateWorkflowSeqAndActionParams = {
    actionId: validateParams.actionId,
    id: validateParams.ruleId,
    project_id: validateParams.project_id,
    ruleId: validateParams.ruleId,
    workflowId: validateParams.workflowId
  };
  var validateResult = {};
  
  validateWorkflowSeqAndAction(validateWorkflowSeqAndActionParams, true)
  .then(function(validateWorkflowSeqActionResult){
    validateResult.collectionSequence = validateWorkflowSeqActionResult.collectionSequence;
    validateResult.processingRule = validateWorkflowSeqActionResult.processingRule;
    validateResult.workflow = validateWorkflowSeqActionResult.workflow;
    
    validateResult.collection = validateWorkflowSeqActionResult.collection;
    //check that the condition specified is on this Action.
    var matchedCondition = conditionModel.getConditionByIdFromActionRootCondition(validateWorkflowSeqActionResult.collection.additional.condition, validateParams.conditionId);
    if(matchedCondition===null){
      throw apiErrorFactory.createNotFoundError("Unable to find matching Condition with ID: "+validateParams.conditionId + " on Action with ID: "+validateParams.actionId);
    }
    validateResult.condition = matchedCondition;
    deferredValidate.resolve(validateResult);
  })
  .fail(function(errorResponse){
    deferredValidate.reject(errorResponse);
  }).done();
  
  return deferredValidate.promise;
}