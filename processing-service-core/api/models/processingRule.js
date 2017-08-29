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
var Q = require('q');
var logger = require('../helpers/loggingHelper.js');
var colSeqModel = require('./policy_api/collectionSequence.js');
var workflowModel = require('./policy_api/workflow.js');
var conditionModel = require('./policy_api/condition.js');
var validationModel = require('./validation.js');
var actionModel = require('./action.js');
var apiErrorFactory = require('./errors/apiErrorFactory.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var ruleObjectsHelper = require('../helpers/processingObjectsHelper.js').rules;

//Retrieves a list of rules on a Workflow. Returns a promise.
module.exports.getRules = function(getRulesParams, responseToWriteTo, errorCallback){
  if(getRulesParams===null || getRulesParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving rules.");
  }
  var totalHits = 0;
  
  var deferredGetRules = Q.defer();
  var getEntriesPromise = workflowModel.getWorkflowEntriesByWorkflowId(getRulesParams.project_id, getRulesParams.workflowId,
    getRulesParams.pageNum, getRulesParams.pageSize);
  
  //keep a map of col.seq. IDs to their priority to persist information between promises.
  var colSeqIdAndPriority = {};
  
  getEntriesPromise.then(function(workflowEntries){
    logger.debug("Retrieved workflow entries to build processing rules. Workflow ID: "+getRulesParams.workflowId);
    //an array of promises will be used to handle when all the col seq. retrieves have completed.
      var colSeqRetrievePromises = [];
      totalHits = workflowEntries.totalhits;
      if(workflowEntries.results===null || workflowEntries.results===undefined || workflowEntries.results.length ===0 ){
        logger.debug("No entries present on the workflow ID: "+getRulesParams.workflowId);
        return colSeqRetrievePromises;
      }      
      for(var sequenceEntry of workflowEntries.results){
        var colSeqId = sequenceEntry.additional.collection_sequence_id;        
        colSeqRetrievePromises.push(colSeqModel.get(getRulesParams.project_id, colSeqId));
        colSeqIdAndPriority[colSeqId] = sequenceEntry.additional.order;
      }
      return colSeqRetrievePromises;
  })
  .all()
  .then(function(colSeqs){
    //if no rules were returned there is a chance the workflow id passed doesn't exist. Validate that the workflow exists (we didn't do this at the start as it would return all the sequence entry information also without any paging and in a case where the Workflow exists we can avoid this performance impact)
    if(colSeqs.length===0){
      return workflowModel.validateWorkflowExists(getRulesParams.project_id, getRulesParams.workflowId);
    }
    
    //convert sequences to rules
    var rules = [];
    for(var colSeq of colSeqs){
      rules.push(policyToProcessingHelper.buildRuleFromCollectionSequenceAndOrder(colSeq, colSeqIdAndPriority[colSeq.id]));
    }
    return Q({rules: rules});
  })
  .then(function(result){
    //if we had rules in the previous step then return them to the user
    if(result.rules){
      deferredGetRules.resolve({
        totalHits: totalHits,
        rules: result.rules
      });
    }
    //if the previous step was validating the workflow and we reached here without an error then return empty rules result.
    else{
      deferredGetRules.resolve({
        totalHits: totalHits,
        rules: []
      });
    }
  })
  .fail(function(errorResponse){
    deferredGetRules.reject(errorResponse);
  }).done();
  
  return deferredGetRules.promise;
};

//CREATE processing rule using Policy API. Returns a promise.
module.exports.createRule = function(createRuleParams){
  if(createRuleParams===null || createRuleParams===undefined){
    throw new Error("Must pass an object of properties to use in creating the rule.");
  }
  
  //retrieve the workflow to get its details so an update can be performed
  var validateWorkflowPromise = workflowModel.validateWorkflowExists(createRuleParams.project_id, createRuleParams.workflowId);
  var workflow;
  
  //hold the created rule to build up and return in response
  var createdRule = {};  

  var deferredCreateRule = Q.defer();
  validateWorkflowPromise.then(function(retrievedWorkflow){
    logger.debug("Verified that workflow exists when creating processing rule. ID: "+createRuleParams.workflowId);
    workflow = retrievedWorkflow;
    //having checked workflow exists and retrieved it, create a col seq. to represent the Rule.
    var collectionSequenceToCreate = {
      name: createRuleParams.name,
      description: createRuleParams.description,
      enabled: createRuleParams.enabled
    };
    return colSeqModel.create(createRuleParams.project_id, collectionSequenceToCreate);
  })
  .then(function(newCollectionSequence){
    logger.debug("Created collection sequence to use in processing rule for workflow with ID: "+createRuleParams.workflowId);
    //add the newly created collection sequence as an entry in the workflow
    var newEntry = workflowModel.insertCollectionSequenceIntoWorkflowEntries(workflow, newCollectionSequence.id, 
      createRuleParams.priority);  
    createdRule = policyToProcessingHelper.buildRuleFromCollectionSequenceAndOrder(newCollectionSequence, newEntry.order);
    
    return workflowModel.updateWorkflow(createRuleParams.project_id, workflow);    
  })
  .then(function(){
    logger.debug("Updated workflow with entry for the created collection sequence. Collection Sequence ID: "+createdRule.id);
    //create a condition associated with this Rule that can be used to add Rule level conditions.    
    var ruleCondition = conditionModel.getNewRuleRootLevelCondition(createdRule.id);
    return conditionModel.create(createRuleParams.project_id, ruleCondition);
  })
  .then(function(){
    logger.debug("Created a condition to use as the processing rule root condition for sequence with ID: "+createdRule.id);
    //rule was created and added to the workflow, output the representation of the Rule
    deferredCreateRule.resolve(createdRule);
  }).fail(function(errorResponse){
    deferredCreateRule.reject(errorResponse);
  }).done();
  
  return deferredCreateRule.promise;
};

//DELETE processing rule using Policy API. Returns a promise.
module.exports.deleteRule = function(deleteRuleParams){
  
  if(deleteRuleParams===null || deleteRuleParams===undefined){
    throw new Error("Must pass an object of properties to use in deleting the rule.");
  }
  //check that the passed in Workflow and Rule (while also retrieving all details) exist.  
  var validatedSequencePromise = colSeqModel.validateSequenceExists(deleteRuleParams.project_id, 
    deleteRuleParams.id, true);
  var validatedWorkflowPromise = workflowModel.validateWorkflowExists(deleteRuleParams.project_id,
    deleteRuleParams.workflowId);
  
  var deferredDeleteRule = Q.defer();
  
  //going to store the workflow the sequence is a part of for use after it is retrieved.
  var workflow = null;
  
  Q.all([validatedSequencePromise, validatedWorkflowPromise]).spread(function(retrievedSeq, workflowResult){
    logger.debug("Verified Workflow and Processing Rule exist before deleting.");
    workflow = workflowResult;
    
    //get priority from the Workflow result to verify that this processing rule is on the specified workflow (sequence_entries should be on the Workflow)
    var rulesMap = ruleObjectsHelper.getRulePrioritiesFromPolicyWorkflow(workflow);
    rulePriority = rulesMap[deleteRuleParams.id];
    if(rulePriority===undefined){      
      throw apiErrorFactory.createNotFoundError("Processing Rule ID: "+ deleteRuleParams.id + " not found on Workflow with ID: "+deleteRuleParams.workflowId);
    }  
    
    //delete any Actions that are under the Rule.    
    return actionModel.deleteActionsUsingFullSeq(deleteRuleParams.project_id, retrievedSeq);
  })
  .then(function(){    
    //remove collection sequence entry from the Workflow
    workflowModel.removeCollectionSequenceFromWorkflowEntries(workflow, deleteRuleParams.id);
    return workflowModel.updateWorkflow(deleteRuleParams.project_id, workflow); 
  })
  .then(function(){
    //delete the root Rule Condition for the Rule, first we need to retrieve the condition with matching notes field
    return conditionModel.getSingleConditionByNotes(deleteRuleParams.project_id, conditionModel.getRuleNotesValue(deleteRuleParams.id));
  })
  .then(function(retrievedCondition){
    //delete this condition (by this point any fragments using it should have been deleted)
    return conditionModel.delete(deleteRuleParams.project_id, retrievedCondition.id);
  })
  .then(function(){
    //delete collection sequence representing the rule
    return colSeqModel.delete(deleteRuleParams.project_id, deleteRuleParams.id);
  })
  .then(function(resultOfDelete){
    var potentialErrorMessage = httpHelper.handleDeleteResponse(resultOfDelete);
    if(potentialErrorMessage!==null){
      deferredDeleteRule.reject(potentialErrorMessage);
    }
    deferredDeleteRule.resolve({});
  })
  .fail(function(errorResponse){
    deferredDeleteRule.reject(errorResponse);
  }).done();
  
  return deferredDeleteRule.promise;
};

//GET processing rule using Policy API. Returns a promise.
module.exports.getRule = function(getRuleParams){
  if(getRuleParams===null || getRuleParams===undefined){
    throw new Error("Must pass parameters for use when retriving a rule.");
  }
  var matchedRule = null;
  
  var deferredGetRule = Q.defer();
  colSeqModel.validateSequenceExists(getRuleParams.project_id, getRuleParams.id)
  .then(function(returnedCollectionSequence){
    matchedRule = policyToProcessingHelper.buildRuleFromCollectionSequence(returnedCollectionSequence);
    //use workflow ID to get priority
    
    return workflowModel.getWorkflowEntriesByWorkflowIdAndCollectionSequenceId(getRuleParams.project_id,
      getRuleParams.workflowId, getRuleParams.id);
  })
  .then(function(retrievedEntries){
    //multiple entries are possible for a single Collection Sequence on a Workflow but this API only allows a single entry per Sequence. Retrieve the first entry and extract priority from it.
    if(retrievedEntries.results.length===0){
      deferredGetRule.reject("Unable to find entry for Rule with ID: "+getRuleParams.id+" on Workflow with ID: "+getRuleParams.workflowId);
      return;
    }

    policyToProcessingHelper.buildRuleFromSequenceEntry(retrievedEntries.results[0], matchedRule);    
    deferredGetRule.resolve(matchedRule);
  })
  .fail(function(errorResponse){
    deferredGetRule.reject(errorResponse);
  }).done();
  return deferredGetRule.promise;
};

//UPDATE processing rule using Policy API. Returns a promise.
module.exports.updateRule = function(updateRuleParams){
  if(updateRuleParams===null || updateRuleParams===undefined){
    throw new Error("Must pass an object of properties to use in updating the rule.");
  }
  
  var updatedRule = null;
  var existingWorkflow = null;
  
  var deferredUpdateRule = Q.defer();
  validationModel.validateWorkflowAndSeqForRule(updateRuleParams)
  .then(function(validationResult){
    logger.debug('Validated that workflow and rule exist before updating rule with ID: '+updateRuleParams.id);
    existingWorkflow = validationResult.workflow;
    var updateCollectionSequence = {
      id: updateRuleParams.id,
      name: updateRuleParams.name,
      description: updateRuleParams.description,
      enabled: updateRuleParams.enabled
    };
    
    return colSeqModel.update(updateRuleParams.project_id, updateCollectionSequence, colSeqModel.defaults.updateBehaviour.add);
  })
  .then(function(updatedCollectionSequence){
    logger.debug('Collection sequence updated as part of updating rule with ID: '+updateRuleParams.id);
    updatedRule = policyToProcessingHelper.buildRuleFromCollectionSequence(updatedCollectionSequence);
    //update the order field on workflow entry for this sequence using the priority passed
    workflowModel.updateOrderOnWorkflowEntries(existingWorkflow, updateRuleParams.id, 
      updateRuleParams.priority);
      
    //use the additional information retrieved to update the Workflow
    var updatePolWorkflowParams = {
      description: existingWorkflow.description,
      id: existingWorkflow.id,
      name: existingWorkflow.name,
      additional: {
        notes: existingWorkflow.notes,
        sequence_entries: existingWorkflow.additional.sequence_entries
      }
    };    
    return workflowModel.update(updateRuleParams.project_id, updatePolWorkflowParams);
  })
  .then(function(){
    logger.debug('Workflow entry updated as part of updating rule with ID: '+updateRuleParams.id);
    updatedRule.priority = updateRuleParams.priority;
    deferredUpdateRule.resolve(updatedRule);
  })
  .fail(function(errorResponse){
    deferredUpdateRule.reject(errorResponse);
  }).done();
  
  return deferredUpdateRule.promise;
};