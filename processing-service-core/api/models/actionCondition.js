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
var logger = require('../helpers/loggingHelper.js');
var colSeqModel = require('./policy_api/collectionSequence.js');
var polWorkflowModel = require('./policy_api/workflow.js');
var collectionModel = require('./policy_api/collection.js');
var conditionModel = require('./policy_api/condition.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var pagingHelper = require('../helpers/pagingHelper.js');
var apiErrorFactory = require('./errors/apiErrorFactory.js');
var ruleObjectsHelper = require('../helpers/processingObjectsHelper.js').rules;
var validationModel = require('./validation.js');

module.exports = {
  create: create,
  delete: deleteActionCondition,
  get: get,
  getConditions: getConditions,
  update: update
};

function deleteActionCondition(deleteParams){
  if(deleteParams===null || deleteParams===undefined){
    throw new Error("Must pass an object of properties to use in deleting Condition on Action.");
  }
    
  var deferredDelete = Q.defer();
  var validationParams = {    
    actionId: deleteParams.actionId,
    conditionId: deleteParams.conditionId,
    ruleId: deleteParams.ruleId,
    project_id: deleteParams.project_id,
    workflowId: deleteParams.workflowId
  };
  
  validationModel.validateWorkflowSeqAndActionForCondition(validationParams)
  .then(function(validationResult){
    logger.debug("Validated workflow, rule, action and condition on action exist when deleting action condition with ID: "+deleteParams.actionId);
    return conditionModel.delete(deleteParams.project_id, deleteParams.conditionId);
  })
  .then(function(resultOfDelete){
    var potentialErrorMessage = httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    logger.debug("Successful delete of Condition with ID: "+ deleteParams.conditionId + " on Action with ID: "+deleteParams.actionId);
    deferredDelete.resolve({});
  })
  .fail(function(errorResponse){
    deferredDelete.reject(errorResponse);
  }).done();
  return deferredDelete.promise;
}

function get(getParams){
  if(getParams===null || getParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving Condition on Action.");
  }
  
  var deferredGetActionCondition = Q.defer();
  var validationParams = {    
    actionId: getParams.actionId,
    conditionId: getParams.conditionId,
    ruleId: getParams.ruleId,
    project_id: getParams.project_id,
    workflowId: getParams.workflowId
  };
  
  validationModel.validateWorkflowSeqAndActionForCondition(validationParams)
  .then(function(validationResult){
    //convert Policy Condition to Processing Condition
    var conditionToReturn = policyToProcessingHelper.buildConditionFromPolicyCondition(validationResult.condition);
    deferredGetActionCondition.resolve(conditionToReturn);
  })
  .fail(function(errorResponse){
    deferredGetActionCondition.reject(errorResponse);
  }).done();
  return deferredGetActionCondition.promise;
}

function getConditions(getParams){
  if(getParams===null || getParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving Conditions on Action.");
  }
  
  var pagingParams = pagingHelper.getValidatedPagingParams(getParams.pageNum, getParams.pageSize);
  //verify the workflow, rule and action exist.
  var validationParams = {    
    id: getParams.ruleId,
    project_id: getParams.project_id,
    workflowId: getParams.workflowId
  };
  var validateWorkflowAndSequencePromise = validationModel.validateWorkflowAndSeqForRule(validationParams);
  //specify to include children when validating collection so it will return the child conditions
  var validatedCollectionPromise = collectionModel.validateCollectionExists(getParams.project_id,
    getParams.actionId, true, "Unable to find Action with ID: "+getParams.actionId);
  
  var deferredGetActionConditions = Q.defer();
  Q.all([validateWorkflowAndSequencePromise, validatedCollectionPromise]).spread(function(validateWorkflowAndSeqResult, validatedCollection){
    if(pagingParams.pageSize === 0){
      //if request was for page size 0 then return no results.
      deferredGetActionConditions.resolve({        
        conditions: [],
        totalHits: 0
      });
      return Q({complete: true});
    }
    
    //retrieved Collection has condition information on it, build up processing conditions from these
    var actionConditions = policyToProcessingHelper.buildActionConditionsFromRootActionPolicyCondition(validatedCollection.additional.condition);
    //paging support is implemented here since the call to retrieve children has no support for paging    
    if(pagingParams.start > actionConditions.length){
      //start index is greater than the number of entries, return empty array.
      deferredGetActionConditions.resolve({        
        conditions: [],
        totalHits: actionConditions.length
      });
      return Q({complete: true});
    }
    var conditionsToReturn = pagingHelper.buildArrayFromPagingParams(actionConditions, pagingParams);
    
    deferredGetActionConditions.resolve({
      conditions: conditionsToReturn,
      totalHits: actionConditions.length
    });
  })
  .fail(function(errorResponse){
    deferredGetActionConditions.reject(errorResponse);
  }).done();
  
  return deferredGetActionConditions.promise;
}

function create(createParams){
  if(createParams===null || createParams===undefined){
    throw new Error("Must pass an object of properties to use in creating Condition on Action.");
  }
  var deferredCreateActionConditions = Q.defer();  
  //call Update Action passing the new condition as a child on the root
  
  //check that the passed in Workflow, Rule and Action exist.    
  var validationParams = {
    actionId: createParams.actionId,
    project_id: createParams.project_id,
    ruleId: createParams.ruleId,
    workflowId: createParams.workflowId
  };
  validationModel.validateWorkflowSeqAndAction(validationParams, true) 
  .then(function(validationResult){
    //update caller condition object with 'type' property for Policy API
    conditionModel.addTypeConditionToCondition(createParams.condition);
    
    var retrievedCollection = validationResult.collection;
    
    //create new condition pointing to the ROOT condition on the Collection
    if(retrievedCollection.additional.condition === null){
      logger.error("Unable to Create Condition. The Collection for ID: "+ createParams.actionId + " has no ROOT condition returned to use as parent.");
      throw "Unable to Create Condition. Action is in invalid state.";
    }
    var rootConditionId = retrievedCollection.additional.condition.id;
    createParams.condition.additional.parent_condition_id = rootConditionId;
    return conditionModel.create(createParams.project_id, createParams.condition);
  })
  .then(function(createdCondition){
    deferredCreateActionConditions.resolve(policyToProcessingHelper.buildConditionFromPolicyCondition(createdCondition));
  })
  .fail(function(errorResponse){
    deferredCreateActionConditions.reject(errorResponse);
  }).done();
    
  return deferredCreateActionConditions.promise;
}

function update(updateParams){
  if(updateParams===null || updateParams===undefined){
    throw new Error("Must pass an object of properties to use in updating Condition on Action.");
  }
  var deferredUpdate = Q.defer();
  //check that the passed in Workflow, Rule and Action exist, and the Action has the specified condition. 
  var validationParams = {    
    actionId: updateParams.actionId,
    conditionId: updateParams.conditionId,
    ruleId: updateParams.ruleId,
    project_id: updateParams.project_id,
    workflowId: updateParams.workflowId
  };
  
  validationModel.validateWorkflowSeqAndActionForCondition(validationParams)
  .then(function(validateResult){
    var matchedCondition = validateResult.condition;
    updateParams.updatedActionCondition.id = updateParams.conditionId;
    //set the parent ID on the updated version of the condition (otherwise this will be updated to be 'isFragment=true' by Policy API)
    updateParams.updatedActionCondition.additional.parent_condition_id = matchedCondition.additional.parent_condition_id;
    //update caller condition object with 'type' property for Policy API
    conditionModel.addTypeConditionToCondition(updateParams.updatedActionCondition);
        
    return conditionModel.update(updateParams.project_id, updateParams.updatedActionCondition);
  })
  .then(function(updateResult){
    deferredUpdate.resolve(updateResult);
  })
  .fail(function(errorResponse){
    deferredUpdate.reject(errorResponse);
  }).done();
    
  return deferredUpdate.promise;
}