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
var polWorkflowModel = require('./policy_api/workflow.js');
var collectionModel = require('./policy_api/collection.js');
var conditionModel = require('./policy_api/condition.js');
var policyModel = require('./policy_api/policy.js');
var policyTypeModel = require('./policy_api/policyType.js');
var validationModel = require('./validation.js');
var pagingHelper = require('../helpers/pagingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');
var apiErrorFactory = require('./errors/apiErrorFactory.js');

module.exports.getActions = function(getActionsParams){
  if(getActionsParams===null || getActionsParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving actions.");
  }
    
  //to be used as a map of Action ID to built up object
  var actionsMap = {};
  var actionsTotalHits = 0;
  var actionsToReturn = [];
  
  //going to use this to allow lookup by policy ID to find the associated Action that has been built so far.
  var policyIdToActionMap = {};
  //going to use this to allow lookup by type ID to find all Actions that need updated with the internal name of the type.
  var typeIdToActionsMap = {};
  
  var deferredGetActions = Q.defer();
  var validationParams = {    
    id: getActionsParams.ruleId,
    project_id: getActionsParams.project_id,
    workflowId: getActionsParams.workflowId
  };
  
  validationModel.validateWorkflowAndSeqForRule(validationParams)
  .then(function(validationResult){
    logger.debug("Validated that workflow and rule exist before retrieving actions.");
    //get details for all collections on the seq
    var retrievedSeq = validationResult.collectionSequence;
    if(retrievedSeq.additional.collection_count===0){
      logger.debug("No collection entries on sequence with ID: "+getActionsParams.ruleId);
      //no collections on the rule, return empty array
      deferredGetActions.resolve({
        totalHits: 0,
        actions: []
      });
      return Q({complete: true});
    }
    actionsTotalHits = retrievedSeq.additional.collection_count;
    
    //only retrieve as many Actions as requested by paging parameters
    var pagingParams = pagingHelper.getValidatedPagingParams(getActionsParams.pageNum, getActionsParams.pageSize);
    
    if(pagingParams.start > retrievedSeq.additional.collection_sequence_entries.length || 
      pagingParams.pageSize === 0){
      //start index is greater than the number of entries, return empty array.
      deferredGetActions.resolve({
        totalHits: actionsTotalHits,
        actions: []
      });
      return Q({complete: true});
    }
    
    var collectionIdsToGet = [];
    var collectionCounter = 0;
    for(var collectionEntry of retrievedSeq.additional.collection_sequence_entries){
      collectionCounter++;
      //only return those entries from paging start index onwards
      if(collectionCounter < pagingParams.start){
        continue;
      }
      
      //Data Processing API does not support multiple collections on a single entry
      if(collectionEntry.collection_ids.length !== 1){
        logger.error("Expected 1 entry on collection sequence entry for sequence ID: "+getActionsParams.ruleId +" but found "+collectionEntry.collection_ids.length);
        throw apiErrorFactory.createError("Unable to retrieve Actions on Rule with ID: "+getActionsParams.ruleId);
      }
      //build Action that will have ID and order properties set using the entry.
      var builtAction = policyToProcessingHelper.buildActionFromCollectionEntry(collectionEntry);
      collectionIdsToGet.push(builtAction.id);
      actionsMap[builtAction.id] = builtAction;
      actionsToReturn.push(builtAction);
      
      //check if we have reached page size limit, if so then no need to add any more collections to retrieve
      if(actionsToReturn.length === pagingParams.pageSize){
        break;
      }
    }
        
    //to retrieve the other properties of this Action we need to request detail about the Collections by their IDs.
    var getWithIdsParams = {
      ids: collectionIdsToGet
    };    
    return collectionModel.getCollectionsByIds(getActionsParams.project_id, getWithIdsParams);
  })
  .then(function(collectionResults){
    //handling case where we don't need to proceed
    if(collectionResults.complete){
      return Q({complete: true});
    }
    
    var policyIdsToGet = [];
    for(var collection of collectionResults.results){
      var builtAction = policyToProcessingHelper.buildActionFromCollection(collection, actionsMap[collection.id]);
      
      if(collection.additional.policy_ids === null || collection.additional.policy_ids === undefined || collection.additional.policy_ids.length ===0){
        logger.warn("No Policy IDs returned for Collection ID: "+collection.id+". Action expects there to be a Policy on the Collection.");
        throw apiErrorFactory.createError('Unable to retrieve the Actions.');
      }
      //record the ID of the Policy associated with this Collection
      var policyIdToGet = collection.additional.policy_ids[0];
      if(policyIdToActionMap[policyIdToGet]===undefined){
        policyIdToActionMap[policyIdToGet] = [builtAction];
      }
      else{
        policyIdToActionMap[policyIdToGet].push(builtAction);
      }
      if(policyIdsToGet.indexOf(policyIdToGet)===-1){
        policyIdsToGet.push(policyIdToGet);
      }
    }
    //retrieve the Policies that were associated with the Collections
    var getPoliciesParams = {
      ids: policyIdsToGet
    };
    return policyModel.getPoliciesByIds(getActionsParams.project_id, getPoliciesParams);
  })
  .then(function(returnedPolicies){
    //handling case where we don't need to proceed
    if(returnedPolicies.complete){
      return Q({complete: true});
    }
    
    var typesToGet = [];
      
    for(var returnedPolicy of returnedPolicies.results){
      //update the built actions with policy information
      var builtActions = policyIdToActionMap[returnedPolicy.id];
      for(var builtAction of builtActions){
        policyToProcessingHelper.buildActionFromPolicy(returnedPolicy, builtAction);
      }
      
      //record the type IDs we need to retrieve
      var typeId = returnedPolicy.additional.policy_type_id;
      if(typesToGet.indexOf(typeId)===-1){
        typesToGet.push(typeId);
      }
      //keep track of all Actions that will need updated with the internal name information once we retrieve it.
      if(typeIdToActionsMap[typeId]===undefined){
        typeIdToActionsMap[typeId] = builtActions;
      }
      else {
        typeIdToActionsMap[typeId] = typeIdToActionsMap[typeId].concat(builtActions);
      }
    }

    //now we have the details of the Policies, we need to get the internal name for their types
    var getTypesParams = {
      ids: typesToGet
    };
    return policyTypeModel.getTypesByIds(getActionsParams.project_id, getTypesParams);    
  })
  .then(function(returnedTypes){
    //handling case where we don't need to proceed
    if(returnedTypes.complete){
      return Q({complete: true});
    }
    
    //update the built actions with the internal name information
    for(var returnedType of returnedTypes.results){
      var builtActions = typeIdToActionsMap[returnedType.id];
      for(var builtAction of builtActions){
        policyToProcessingHelper.buildActionFromPolicyType(returnedType, builtAction);
      }
    }
    deferredGetActions.resolve({
      totalHits: actionsTotalHits,
      actions: actionsToReturn
    });
  })
  .fail(function(errorResponse){
    deferredGetActions.reject(errorResponse);
  }).done();
  return deferredGetActions.promise;
};

module.exports.getAction = function(getActionParams){
  if(getActionParams===null || getActionParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving action.");
  }
  
  var actionToReturn = {};
  var deferredGetAction = Q.defer();
  
  var validationParams = {    
    id: getActionParams.ruleId,
    project_id: getActionParams.project_id,
    workflowId: getActionParams.workflowId
  };
  
  validationModel.validateWorkflowAndSeqForRule(validationParams)
  .then(function(validationResult){
    logger.debug("Validated workflow and processing rule exist when retrieving action with ID: "+getActionParams.id);
    var retrievedSeq = validationResult.collectionSequence;
    //retrieve the collection entry on the sequence based on Rule ID passed, then retrieve the collection based on the ID of the action requested, then retrieve the Policy on that Collection (note Action only supports a single Policy)    
    if(retrievedSeq.additional.collection_sequence_entries.length===0){
      logger.debug("No collection entries on sequence with ID: "+getActionParams.ruleId);
      throw apiErrorFactory.createNotFoundError("There are no Actions on Rule with ID: "+getActionParams.ruleId);      
    }

    //iterate over collection entries to find the match for the ID requested
    for(var collectionEntry of retrievedSeq.additional.collection_sequence_entries){
      if(collectionEntry.collection_ids[0]!==getActionParams.id){
        continue;
      }
      policyToProcessingHelper.buildActionFromCollectionEntry(collectionEntry, actionToReturn);
      break;
    }
    if(actionToReturn.id===undefined){
      logger.debug("Unable to find a collection entry on Collection Sequence ID: "+getActionParams.ruleId +
        " with Collection ID: "+getActionParams.id);
        throw apiErrorFactory.createNotFoundError("Unable to retrieve Action. No matching Action with ID: "+getActionParams.id+
          " found on Rule ID: "+getActionParams.ruleId);
    }
    //retrieve more detail about the Collection representing Action
    return collectionModel.get(getActionParams.project_id, getActionParams.id);
  })
  .then(function(returnedCollection){
    //update the action with the appropriate properties from the returned collection
    policyToProcessingHelper.buildActionFromCollection(returnedCollection, actionToReturn);
    
    if(returnedCollection.additional.policy_ids === null || returnedCollection.additional.policy_ids === undefined || returnedCollection.additional.policy_ids.length ===0){
      logger.warn("No Policy IDs returned for Collection ID: "+returnedCollection.id+". Action expects there to be a Policy on the Collection.");
      throw apiErrorFactory.createError('Unable to retrieve the Action.');
    }
    
    var policyId = returnedCollection.additional.policy_ids[0];
    return policyModel.get(getActionParams.project_id, policyId);    
  })
  .then(function(returnedPolicy){
    policyToProcessingHelper.buildActionFromPolicy(returnedPolicy, actionToReturn);
    //use the policy type ID to retrieve the internal name of the Type
    return policyTypeModel.get(getActionParams.project_id, returnedPolicy.additional.policy_type_id);
  }).
  then(function(returnedPolicyType){
    policyToProcessingHelper.buildActionFromPolicyType(returnedPolicyType, actionToReturn);    
    deferredGetAction.resolve(actionToReturn);
  })
  .fail(function(errorResponse){
    deferredGetAction.reject(errorResponse);
  }).done();
  
  return deferredGetAction.promise;
};

module.exports.updateAction = function(updateActionParams){
  if(updateActionParams===null || updateActionParams===undefined){
    throw new Error("Must pass an object of properties to use in updating action.");
  }
    
  var actionToReturn = {};
  var policyIdOnCollection = null;
  
  var deferredUpdateAction = Q.defer();
  var validationParams = {    
    id: updateActionParams.ruleId,
    project_id: updateActionParams.project_id,
    workflowId: updateActionParams.workflowId
  };
  
  validationModel.validateWorkflowAndSeqForRule(validationParams)
  .then(function(validationResult){
    logger.debug("Validated workflow and processing rule exist when updating action with ID: "+updateActionParams.id);
    var retrievedSeq = validationResult.collectionSequence;
    if(retrievedSeq.additional.collection_sequence_entries.length===0){
      logger.debug("No collection entries on sequence with ID: "+updateActionParams.ruleId);
      throw apiErrorFactory.createNotFoundError("There are no Actions on Rule with ID: "+updateActionParams.ruleId);      
    }
    var foundActionOnSeq = false;
    //iterate over collection entries to find the match for the ID requested
    for(var collectionEntry of retrievedSeq.additional.collection_sequence_entries){
      if(collectionEntry.collection_ids[0]!==updateActionParams.id){
        continue;
      }
      //update the order field to be what was passed by caller
      collectionEntry.order = updateActionParams.order;      
      policyToProcessingHelper.buildActionFromCollectionEntry(collectionEntry, actionToReturn);
      foundActionOnSeq = true;
      break;
    }
    if(foundActionOnSeq===false){
      logger.debug("Could not find a collection with the specified ID on the collection sequence. ID: "+updateActionParams.id);
      throw apiErrorFactory.createNotFoundError("Could not find Action with ID: "+updateActionParams.ruleId +" on Rule with ID: " + updateActionParams.ruleId);
    }
    //save the collection sequence with the updated order field
    var updateCollectionSequenceParams = {
      additional: retrievedSeq.additional,
      enabled: retrievedSeq.additional.evaluation_enabled,
      id: retrievedSeq.id,
      name: retrievedSeq.name,
      description: retrievedSeq.description
    };
    return colSeqModel.update(updateActionParams.project_id, updateCollectionSequenceParams);
  })
  .then(function(){
    logger.debug("Updated collection entry on collection sequence as part of updating action with ID: "+updateActionParams.id);
    //with collection entry updated retrieve the collection details
    return collectionModel.validateCollectionExists(updateActionParams.project_id, updateActionParams.id, false, "Unable to find Action with ID: "+updateActionParams.id);
  })
  .then(function(retrievedCollection){
    if(retrievedCollection.additional.policy_ids.length === 0) {
      logger.info("Collection with ID: "+updateActionParams.id+ " has no policy IDs returned on retrieve. A new Policy will be created and used for the Action.");
      //since there was no Policy ID on the collection we will create one now to get the Action into a proper state.
      var createPolicyParams = {
        description: "",
        details: updateActionParams.settings,
        name: updateActionParams.name,
        priority: 0,
        typeId: updateActionParams.typeId
      };
      return policyModel.create(updateActionParams.project_id, createPolicyParams); 
    }
    
    var updatePolicyParams = {
      description: "",
      details: updateActionParams.settings,
      id: retrievedCollection.additional.policy_ids[0],
      name: updateActionParams.name,
      priority: 0,
      typeId: updateActionParams.typeId
    };
    return policyModel.update(updateActionParams.project_id, updatePolicyParams); 
  })
  .then(function(updatePolicy){
    policyIdOnCollection = updatePolicy.id;
    
    actionToReturn = policyToProcessingHelper.buildActionFromPolicy(updatePolicy, actionToReturn);
    var updateCollectionParams = {
      description: updateActionParams.description,
      id: updateActionParams.id,
      name: updateActionParams.name,
      policyIds: [policyIdOnCollection]
    };
    return collectionModel.update(updateActionParams.project_id, updateCollectionParams);
  })
  .then(function(updatedCollection){
    actionToReturn = policyToProcessingHelper.buildActionFromCollection(updatedCollection, actionToReturn);    
    //get the internal_name of the policy type for inclusion on the returned Action.
    return policyTypeModel.get(updateActionParams.project_id, updateActionParams.typeId);
  })
  .then(function(retrievedPolicyType){  
    deferredUpdateAction.resolve(actionToReturn);
  })
  .fail(function(errorResponse){
    deferredUpdateAction.reject(errorResponse);
  }).done();
  
  return deferredUpdateAction.promise;
};

module.exports.createAction = function(createActionParams){
  if(createActionParams===null || createActionParams===undefined){
    throw new Error("Must pass an object of properties to use in creating action.");
  }
  
  var actionToReturn = {};
  var collectionSequenceToAddTo = null;
  var ruleConditionId = null;
  var deferredCreateAction = Q.defer();
  
  var validationParams = {    
    id: createActionParams.ruleId,
    project_id: createActionParams.project_id,
    workflowId: createActionParams.workflowId
  };
  
  validationModel.validateWorkflowAndSeqForRule(validationParams)
  .then(function(validationResult){
    logger.debug("Validated workflow and processing rule exist when creating action");    
    collectionSequenceToAddTo = validationResult.collectionSequence;
    //retrieve the Rule level condition to add as a child condition on the Collection that is created.
    return conditionModel.getSingleConditionByNotes(createActionParams.project_id, conditionModel.getRuleNotesValue(createActionParams.ruleId));
  })
  .then(function(retrievedRuleCondition){
    logger.debug("Retrieved rule condition to include on action being created.");
    ruleConditionId = retrievedRuleCondition.id;    
    //create a Policy for use as part of the action
    
    var createPolicyParams = {
      name: createActionParams.name,
      description: "", //Only recording description on the Collection created. Saves recording more duplicate information where possible.
      details: createActionParams.settings,
      priority: 0, //action should only have one Policy on it so always passing 0 for priority.
      typeId: createActionParams.typeId
    };
        
    return policyModel.create(createActionParams.project_id, createPolicyParams);
  })
  .then(function(createdPolicy){
    logger.debug("Created Policy for use with Action being created. Policy ID: "+createdPolicy.id);
    actionToReturn = policyToProcessingHelper.buildActionFromPolicy(createdPolicy, actionToReturn);
    
    //create a collection that uses this Policy    
    var createCollectionParams = {
      description: createActionParams.description,
      name: createActionParams.name,
      policyIds: [createdPolicy.id]
    };
    //add a condition to act as a Root that other conditions can be attached to. By default this will match all documents.
    createCollectionParams.condition = conditionModel.getNewActionRootLevelCondition();
    
    //create a fragment condition pointing to the Rule level condition
    var fragmentPointer = conditionModel.getNewFragmentCondition(ruleConditionId);    
    conditionModel.addChildToCondition(createCollectionParams.condition, fragmentPointer);
    
    return collectionModel.create(createActionParams.project_id, createCollectionParams);
  })
  .then(function(createdCollection){
    logger.debug("Created collection to represent Action. ID: "+createdCollection.id);
    actionToReturn = policyToProcessingHelper.buildActionFromCollection(createdCollection, actionToReturn);
    
    //add this collection to the requested rule (col seq)
    return colSeqModel.updateCollectionSequenceWithEntryForCollection(createActionParams.project_id,
      collectionSequenceToAddTo, createdCollection.id, createActionParams.order,
      colSeqModel.defaults.updateBehaviour.add);    
  })
  .then(function(){
    logger.debug("Updated collection sequence with an entry for the new action. Action ID: "+actionToReturn.id);
    actionToReturn.order = createActionParams.order;
    
    //get the internal_name of the policy type for inclusion on the returned Action.
    return policyTypeModel.get(createActionParams.project_id, createActionParams.typeId);
  })
  .then(function(retrievedPolicyType){
    logger.debug("Retrieved the internal name of the policy type to return on the action.");
    actionToReturn = policyToProcessingHelper.buildActionFromPolicyType(retrievedPolicyType, actionToReturn);
    deferredCreateAction.resolve(actionToReturn);
  })
  .fail(function(errorResponse){
    deferredCreateAction.reject(errorResponse);
  }).done();
  
  return deferredCreateAction.promise;
};

//a version of deleteAction that removes all Actions under a passed 'full detail' Collection Sequence (one that was retrieved using 'include_children' from Policy API).
module.exports.deleteActionsUsingFullSeq = function(projectId, existingSeqDetails){
  var deferredDeleteActions = Q.defer();
  
  //1) Remove Collection Entry for each Collection from associated Collection Sequence
  //2) Update Actions to not have Policies
  //3) Delete Policies that were on the Actions
  //4) Delete Collections
  
  //'include_children' version of sequence is structured differently, retrieve the sequence to use in update from property on 'additional'
  var sequenceForUpdate = existingSeqDetails.additional.collection_sequences[0];

  var collectionIds = [];
  var rootConditionIds = [];
  
  //remove collection entries from sequence
  sequenceForUpdate.additional.collection_sequence_entries = [];    
  colSeqModel.update(projectId, sequenceForUpdate)
  .then(function(){
    logger.debug('Updated sequence with ID: '+sequenceForUpdate.id + ' removing Collection Entries');
    
    //remove Policies from each Collection    
    var updateCollectionPromises = [];
    for(var collection of existingSeqDetails.additional.collections){
      var updatedCollection = {
        description: collection.description,
        id: collection.id,
        name: collection.name,
        policyIds: []
      };
      collectionIds.push(collection.id);
      rootConditionIds.push(collection.additional.condition.id);
      updateCollectionPromises.push(collectionModel.update(projectId, updatedCollection));
    }
    return updateCollectionPromises;
  })
  .all()
  .then(function(){
    logger.debug('Removed Policies from Collections that were on Sequence with ID: '+ sequenceForUpdate.id);
    //delete the ROOT conditions that are on the Collections
    return conditionModel.deleteAll(projectId, rootConditionIds);
  })
  .then(function(resultOfDelete){
    //if Policy API can't delete it returns 200 status and a body conveying error.
    httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    logger.debug('Deleted all conditions that were on the Collections on Sequence with ID: '+sequenceForUpdate.id);
    //with the Policies off the Collections, delete all the Collections
    return collectionModel.deleteAll(projectId, collectionIds);
  })
  .then(function(resultOfDelete){
    //if Policy API can't delete it returns 200 status and a body conveying error.
    httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    logger.debug('Deleted all Collections that were on Sequence with ID: '+sequenceForUpdate.id);
    
    var policyIds = [];
    //delete all the Policies
    for(var policy of existingSeqDetails.additional.policies){
      policyIds.push(policy.id);
    }
    return policyModel.deleteAll(projectId, policyIds);
  })
  .then(function(resultOfDelete){
    //if Policy API can't delete it returns 200 status and a body conveying error.
    httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    deferredDeleteActions.resolve({});
  })
  .fail(function(errorResponse){
    deferredDeleteActions.reject(errorResponse);
  })
  .done();
  
  return deferredDeleteActions.promise;
};

module.exports.deleteAction = function(deleteActionParams){
  if(deleteActionParams===null || deleteActionParams===undefined){
    throw new Error("Must pass an object of properties to use in deleting an action.");
  }
  
  var deferredDeleteAction = Q.defer();
  
  var validationParams = {    
    id: deleteActionParams.ruleId,
    project_id: deleteActionParams.project_id,
    workflowId: deleteActionParams.workflowId
  };
  
  var removeFromSequencePromise = validationModel.validateWorkflowAndSeqForRule(validationParams)
  .then(function(validationResult){
    logger.debug("Validated workflow and processing rule exist when deleting action");
    var retrievedSeq = validationResult.collectionSequence;
    //remove the Collection Entry from the Collection Sequence
    colSeqModel.removeCollectionEntry(retrievedSeq.additional.collection_sequence_entries, deleteActionParams.id);
    
    //save this updated collection sequence
    var updateCollectionSequenceParams = {
      additional: retrievedSeq.additional,
      id: retrievedSeq.id,
      name: retrievedSeq.name,
      description: retrievedSeq.description
    };    
    return colSeqModel.update(deleteActionParams.project_id, updateCollectionSequenceParams);
  })
  .then(function(){
    logger.debug('Removed Collection Entry with Collection ID: '+deleteActionParams.id+' from Collection Sequence with ID: '+deleteActionParams.ruleId);
    return Q(deleteActionParams.id);
  });
  
  continueDeleteActionAfterRemovalFromRule(deleteActionParams.project_id, removeFromSequencePromise)
  .then(function(result){
    deferredDeleteAction.resolve({});
  })
  .fail(function(errorResponse){
    deferredDeleteAction.reject(errorResponse);
  })
  .done();
  
  return deferredDeleteAction.promise;
};

//takes in a promise and on its resolution (that provides an Action ID) will Delete an Action with the assumption it has been removed from a Rule already. This allows re-use of this logic for single Action and multiple Action delete.
function continueDeleteActionAfterRemovalFromRule(projectId, existingPromise){
  //using these for communication across promises
  var policyIdOnCollection = null;
  var conditionIdOnCollection = null;
  var actionId = null;
  
  return existingPromise.then(function(passedActionId){
    actionId = passedActionId;
    
  //need to retrieve the collection to find the ID of the Policy on it so we can delete the Policy later.
    return collectionModel.validateCollectionExists(projectId, actionId, true, "Unable to find Action with ID: "+actionId);
  })
  .then(function(retrievedCollection){
    logger.debug('Retrieved Collection with ID: '+actionId);
    
    //store condition ID for later deletion
    conditionIdOnCollection = retrievedCollection.additional.condition !== null ?
      retrievedCollection.additional.condition.id : null;
          
    if(retrievedCollection.additional.policy_ids.length === 0) {
      //possible that deleting the collection failed after removing the Policy from collection
      logger.debug('No Policy to remove from Collection with ID: '+ actionId);
      return Q(retrievedCollection);
    }
    //store policy ID for later deletion, we need to get it off the collection first.
    policyIdOnCollection = retrievedCollection.additional.policy_ids[0];        
    
    //remove the policy from the collection and call update for the collection
    var updatedCollection = {
      description: retrievedCollection.description,
      id: retrievedCollection.id,
      name: retrievedCollection.name,
      policyIds: []
    };
    return collectionModel.update(projectId, updatedCollection);
  })
  .then(function(updatedCollection){
    if(policyIdOnCollection!==null){
      logger.debug('Removed Policy ID: '+policyIdOnCollection+' from Collection with ID: '+updatedCollection.id);
    }    
    //with the policy off the collection, delete the collection
    return collectionModel.delete(projectId, updatedCollection.id);
  })
  .then(function(resultOfDelete){
    //if Policy API can't delete the Collection it returns 200 status and a body conveying error.
    httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    
    logger.debug('Deleted Collection with ID: '+actionId);
    
    if(conditionIdOnCollection===null){
      //no condition on the collection to remove
      logger.debug('Collection with ID '+actionId + " has no condition to delete.");
      return Q();
    }
    
    //delete the conditions that were on the collection    
    return conditionModel.delete(projectId, conditionIdOnCollection);
  })
  .then(function(resultOfDelete){        
    if(conditionIdOnCollection!==null){
      httpHelper.handleDeleteResponseAndThrow(resultOfDelete);    
      logger.debug('Deleted Condition with ID: '+conditionIdOnCollection);
    }
    if(policyIdOnCollection!==null){
      return policyModel.delete(projectId, policyIdOnCollection);
    }
    else{
      logger.debug('No Policy to delete for Collection with ID: '+actionId);
      return Q();
    }
  })
  .then(function(resultOfDelete){
    if(policyIdOnCollection!==null){
      httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
      logger.debug('Deleted Policy with ID: '+policyIdOnCollection+ ' that was on Collection ID: '+actionId);
    }
    //with collection and policy deleted write the success response.
    return Q({});
  });
}