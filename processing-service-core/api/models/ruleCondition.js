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
var Q = require('q');
var logger = require('../helpers/loggingHelper.js');
var colSeqModel = require('./policy_api/collectionSequence.js');
var polWorkflowModel = require('./policy_api/workflow.js');
var collectionModel = require('./policy_api/collection.js');
var conditionModel = require('./policy_api/condition.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var pagingHelper = require('../helpers/pagingHelper.js');
var apiErrorFactory = require('./errors/apiErrorFactory.js');

module.exports = {
  create: create,
  delete: deleteCondition,
  get: get,
  getConditions: getConditions,
  update: update
};

//convenience method to find the Root Condition for a Rule. Pass includeChildren as true to return child conditions. Returns a promise.
function getRuleRootCondition(projectId, ruleId){
  return conditionModel.getSingleConditionByNotes(projectId, conditionModel.getRuleNotesValue(ruleId));
}

function update(updateParams){
  if(updateParams===null || updateParams===undefined){
    throw new Error("Must pass an object of properties to use in creating Condition on Rule.");
  }
  var deferredUpdate = Q.defer();
  
  //verify the workflow and rule exist.
  var getRootConditionPromise = validateAndGetRootCondition(updateParams.project_id, updateParams.workflowId, updateParams.ruleId);
  
  getRootConditionPromise.then(function(retrievedRootCondition){
    logger.debug("Retrieved Rule Root condition with children for Rule ID: "+updateParams.ruleId);
    
    var matchedCondition = conditionModel.getConditionByIdFromRuleRootCondition(retrievedRootCondition, updateParams.conditionId);
    if(matchedCondition===null){
      throw apiErrorFactory.createNotFoundError("Unable to find matching Condition with ID: "+updateParams.conditionId);
    }
    //prepare condition provided to be sent to Policy API.
    updateParams.updatedRuleCondition.id = updateParams.conditionId;
    //set the parent ID on the updated version of the condition (otherwise this will be updated to be 'isFragment=true' by Policy API)
    updateParams.updatedRuleCondition.additional.parent_condition_id = matchedCondition.additional.parent_condition_id;
    conditionModel.addTypeConditionToCondition(updateParams.updatedRuleCondition);
    return conditionModel.update(updateParams.project_id, updateParams.updatedRuleCondition);
  })
  .then(function(updateResult){
    deferredUpdate.resolve(updateResult);
  })
  .fail(function(errorResponse){
    deferredUpdate.reject(errorResponse);
  }).done();
  
  return deferredUpdate.promise;
}

function create(createParams){
  if(createParams===null || createParams===undefined){
    throw new Error("Must pass an object of properties to use in creating Condition on Rule.");
  }
  var deferredCreateRuleCondition = Q.defer();
  
  //validate Workflow and Rule exist (get all details on the Rule at the same time so we get the IDs of the Rule condition for later
  var validatedSequencePromise = colSeqModel.validateSequenceExists(createParams.project_id, 
    createParams.ruleId, true);
  var validatedWorkflowPromise = polWorkflowModel.validateWorkflowExists(createParams.project_id,
    createParams.workflowId);
    
  Q.all([validatedSequencePromise, validatedWorkflowPromise]).spread(function(retrievedSeq, retrievedWorkflow){
    logger.debug(function(){return "Retrieved Workflow: "+JSON.stringify(retrievedWorkflow);});
    logger.debug(function(){return "Retrieved Collection Sequence: "+JSON.stringify(retrievedSeq);});
    
    //get the Rule Root condition if it is available on the retrieved Sequence
    var ruleCondition = conditionModel.getRuleConditionFromDetailedCollectionSequence(retrievedSeq);
    if(ruleCondition!==null){
      return Q(ruleCondition);
    }
    //if it wasn't returned on the sequence then query for it
    return getRuleRootCondition(createParams.project_id, createParams.ruleId);
  })
  .then(function(ruleCondition){
    logger.debug(function(){return "Retrieved Rule Root Condition: "+JSON.stringify(ruleCondition);});
    var ruleParentConditionId = ruleCondition.id;
    
    //update caller condition object with 'type' property for Policy API
    conditionModel.addTypeConditionToCondition(createParams.condition);
    
    //create the condition the caller specified. It will exist under the Rule Root condition.
    createParams.condition.additional.parent_condition_id = ruleParentConditionId;
    return conditionModel.create(createParams.project_id, createParams.condition);
  })
  .then(function(createdCondition){
    logger.debug(function(){return "Created condition under the Rule with ID: " + createParams.ruleId + ". Condition: " +JSON.stringify(createdCondition);});
    deferredCreateRuleCondition.resolve(policyToProcessingHelper.buildConditionFromPolicyCondition(createdCondition));
  })
  .fail(function(errorResponse){
    deferredCreateRuleCondition.reject(errorResponse);
  }).done();
    
  return deferredCreateRuleCondition.promise;
}

function get(getParams){
  if(getParams===null || getParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving Conditions on Rule.");
  }
  var deferredGetRuleCondition = Q.defer();
    
  var getRootConditionPromise = validateAndGetRootCondition(getParams.project_id, getParams.workflowId, getParams.ruleId);
  
  getRootConditionPromise.then(function(retrievedRootCondition){    
    var matchedCondition = conditionModel.getConditionByIdFromRuleRootCondition(retrievedRootCondition, getParams.conditionId);
    if(matchedCondition===null){
      throw apiErrorFactory.createNotFoundError("Unable to find matching Condition with ID: "+getParams.conditionId);
    }
    //convert Policy Condition to Processing Condition
    var conditionToReturn = policyToProcessingHelper.buildConditionFromPolicyCondition(matchedCondition);
    deferredGetRuleCondition.resolve(conditionToReturn);
  })
  .fail(function(errorResponse){
    deferredGetRuleCondition.reject(errorResponse);
  }).done();
  
  return deferredGetRuleCondition.promise;
}

//validates a Workflow and Collection Sequence exist before finding the Rule root condition (as returned by Policy API, with child details). Returns a promise.
function validateAndGetRootCondition(projectId, workflowId, ruleId){
  var validateAndGetRootConditionPromise = Q.defer();
  
  //verify the workflow and rule exist.
  var validatedSequencePromise = colSeqModel.validateSequenceExists(projectId, 
    ruleId);
  var validatedWorkflowPromise = polWorkflowModel.validateWorkflowExists(projectId,
    workflowId);
  Q.all([validatedSequencePromise, validatedWorkflowPromise]).spread(function(retrievedSeq, retrievedWorkflow){
    logger.debug(function(){return "Retrieved Workflow: "+JSON.stringify(retrievedWorkflow);});
    logger.debug(function(){return "Retrieved Collection Sequence: "+JSON.stringify(retrievedSeq);});
    //retrieve the 'isFragment' Root condition for the Rule by its Notes field.
    return getRuleRootCondition(projectId, ruleId);
  })
  .then(function(retrievedRootCondition){
    logger.debug("Found Rule Root condition (without children) for Rule ID: "+ruleId);
    //Policy API filtering of conditions won't return the child conditions on the result so retrieve the condition by its ID stating to include children.
    return conditionModel.get(projectId, retrievedRootCondition.id, true);
  })
  .then(function(fullRootCondition){
    logger.debug("Retrieved Rule Root condition (with children) for Rule ID: "+ruleId);
    validateAndGetRootConditionPromise.resolve(fullRootCondition);
  })
  .fail(function(errorResponse){
    validateAndGetRootConditionPromise.reject(errorResponse);
  });
  
  return validateAndGetRootConditionPromise.promise;
}

function getConditions(getParams){
  if(getParams===null || getParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving Conditions on Rule.");
  }
  var deferredGetRuleConditions = Q.defer();
  var pagingParams = pagingHelper.getValidatedPagingParams(getParams.pageNum, getParams.pageSize);
    
  //verify the workflow and rule exist.
  var validatedSequencePromise = colSeqModel.validateSequenceExists(getParams.project_id, 
    getParams.ruleId);
  var validatedWorkflowPromise = polWorkflowModel.validateWorkflowExists(getParams.project_id,
    getParams.workflowId);
  
  Q.all([validatedSequencePromise, validatedWorkflowPromise]).spread(function(retrievedSeq, retrievedWorkflow){
    if(pagingParams.pageSize === 0){
      //if request was for page size 0 then return no results.
      deferredGetRuleConditions.resolve({        
        conditions: [],
        totalHits: 0
      });
      return Q({complete: true});
    }
    
    //retrieve the 'isFragment' Root condition for the Rule by its Notes field.
    return getRuleRootCondition(getParams.project_id, getParams.ruleId);
  })
  .then(function(ruleRootConditon){
    //handling case where we don't need to proceed
    if(ruleRootConditon.complete){
      return Q({complete: true});
    }
    
    //Policy API filtering of conditions won't return the child conditions on the result so retrieve the condition by its ID stating to include children.
    return conditionModel.get(getParams.project_id, ruleRootConditon.id, true);
  })
  .then(function(ruleRootConditon){
    //handling case where we don't need to proceed
    if(ruleRootConditon.complete){
      return Q({complete: true});
    }
    
    //convert to Processing API Condition representation (including not exposing the Root Rule condition)
    var conditionsOnRule = policyToProcessingHelper.buildRuleConditionsFromRootRulePolicyCondition(ruleRootConditon);    
    //paging support is implemented here since the call to retrieve children has no support for paging    
    if(pagingParams.start > conditionsOnRule.length){
      //start index is greater than the number of entries, return empty array.
      deferredGetRuleConditions.resolve({        
        conditions: [],
        totalHits: conditionsOnRule.length
      });
      return Q({complete: true});
    }
    var conditionsToReturn = pagingHelper.buildArrayFromPagingParams(conditionsOnRule, pagingParams);
    
    deferredGetRuleConditions.resolve({
      conditions: conditionsToReturn,
      totalHits: conditionsOnRule.length
    });
  })
  .fail(function(errorResponse){
    deferredGetRuleConditions.reject(errorResponse);
  }).done();
    
  return deferredGetRuleConditions.promise;
}

function deleteCondition(deleteParams){
  if(deleteParams===null || deleteParams===undefined){
    throw new Error("Must pass an object of properties to use when deleting Condition on Rule.");
  }
  var deferredDelete = Q.defer();
    
  var getRootConditionPromise = validateAndGetRootCondition(deleteParams.project_id, deleteParams.workflowId, deleteParams.ruleId);
  getRootConditionPromise.then(function(retrievedRootCondition){    
    var matchedCondition = conditionModel.getConditionByIdFromRuleRootCondition(retrievedRootCondition, deleteParams.conditionId);
    if(matchedCondition===null){
      throw apiErrorFactory.createNotFoundError("Unable to find matching Condition with ID: "+deleteParams.conditionId);
    }
    return conditionModel.delete(deleteParams.project_id, matchedCondition.id);
  })
  .then(function(){
    logger.debug("Successfully deleted Condition with ID: "+deleteParams.conditionId+" from Rule with ID: "+deleteParams.ruleId);
    deferredDelete.resolve({});
  })
  .fail(function(errorResponse){
    deferredDelete.reject(errorResponse);
  }).done();
  return deferredDelete.promise;
}