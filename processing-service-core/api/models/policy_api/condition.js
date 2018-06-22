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
var policyApiHelper = require('../../helpers/policyApiHelpers.js');
var logger = require('../../helpers/loggingHelper.js');

//exporting constant values used in condition fields for use elsewhere.
var conditionValues = {
  ACTION_ROOT: "ACTION_ROOT",
  RULE_PREFIX: "RULE_ID:",
  ACTION_RULE_FRAGMENT: "ACTION_RULE_FRAGMENT"
};

module.exports = {
  addChildToCondition: addChildToCondition,
  addTypeConditionToCondition: addTypeConditionToCondition,
  checkConditionHasChildrenProperty: checkConditionHasChildrenProperty,
  conditionValues: conditionValues,
  create: create,
  delete: deleteCondition,
  deleteAll: deleteAll,
  get: get,  
  getNewActionRootLevelCondition: getNewActionRootLevelCondition,
  getConditionByIdFromActionRootCondition: getConditionByIdFromActionRootCondition,
  getConditionByIdFromRuleRootCondition: getConditionByIdFromRuleRootCondition,
  getNewFragmentCondition: getNewFragmentCondition,
  getNewRuleRootLevelCondition: getNewRuleRootLevelCondition,
  getRuleConditionFromDetailedCollectionSequence: getRuleConditionFromDetailedCollectionSequence,
  getRuleNotesValue: getRuleNotesValue,
  getSingleConditionByNotes: getSingleConditionByNotes,
  update: update
};

//returns a params object with common parameters for Collection. Takes in a project ID and uses that in the params.
var getDefaultParams = function(projectId){
  return {
    project_id: projectId,
    type: "condition"
  };
};

function update(projectId, condition){
  var updateParams = getDefaultParams(projectId);
  updateParams.id = condition.id;
  updateParams.name = condition.name;
  updateParams.additional = condition.additional;
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/update", updateParams);
}

//create a condition using passed information. Returns a promise.
function create(projectId, condition){
  var createConditionParams = getDefaultParams(projectId);
  createConditionParams.name = condition.name;
  createConditionParams.notes = condition.notes;
  createConditionParams.additional = condition.additional;
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/create", createConditionParams);
}

//get a condition with the specified ID. Returns a promise.
function get(projectId, conditionId, includeChildren){
  var getConditionParams = getDefaultParams(projectId);
  getConditionParams.id = conditionId;
  getConditionParams.additional = {};
  if(includeChildren){
    getConditionParams.additional.include_children = true;
  }
  else {
    getConditionParams.additional.include_children = false;
  }
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemRequest("classification/retrieve", getConditionParams);
}

//gets the first condition whose Notes field matches the value passed.
function getSingleConditionByNotes(projectId, notesValue){
  var getConditionParams = getDefaultParams(projectId);
  getConditionParams.additional = {
    filter: {
      notes: notesValue
    }
  };
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemRequest("classification/retrieve", getConditionParams);
}

//deletes a condition (and its children) with the specified ID. Returns a promise.
function deleteCondition(projectId, conditionId){
  var deleteConditionParams = getDefaultParams(projectId);
  deleteConditionParams.id = conditionId;
  
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/delete", deleteConditionParams);
}

//deletes all conditions for the IDs passed. Returns a promise.
function deleteAll(projectId, conditionIds){
  var deleteConditionParams = getDefaultParams(projectId);
  deleteConditionParams.id = conditionIds;
  
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/delete", deleteConditionParams);
}

//Returns an object that represents a Policy Condition with a root condition referring to Action that will always match any document evaluated against it. Additional conditions should be added as children of the root.
function getNewActionRootLevelCondition(){
  return {
    "type": "condition",
    "name": conditionValues.ACTION_ROOT,
    "additional": {
      "type": "boolean",
      "operator": "and",
      "children": [
      ]
    },
    "notes": conditionValues.ACTION_ROOT
  };
}

//Returns an object that represents a Policy Condition with a root condition referring to a specified Rule that will always match any document evaluated against it. Additional conditions should be added as children of the root.
function getNewRuleRootLevelCondition(ruleId){
  return {
    "type": "condition",
    "name": getRuleNotesValue(ruleId),
    "additional": {
      "type": "boolean",
      "operator": "and",
      "children": [
      ]
    },
    "notes": getRuleNotesValue(ruleId)
  };
}

function getNewFragmentCondition(conditionToPointTo){
  return {
    "type": "condition",
    "name": conditionValues.ACTION_RULE_FRAGMENT,
    "additional": {
      "type": "fragment",
      "value": conditionToPointTo
    },
    "notes": conditionValues.ACTION_RULE_FRAGMENT
  };
}

//Returns the value that would be in a Root Rule Condition notes field for the specified Rule ID
function getRuleNotesValue(ruleId){
  return conditionValues.RULE_PREFIX + ruleId;
}

//convinience method to add a condition to another condition object as a child. Updates the object passed in.
function addChildToCondition(conditionToAddTo, childCondition){
  if(!conditionToAddTo.hasOwnProperty('additional')){
    throw new Error("Condition must have 'additional' property defined.");
  }
  if(!conditionToAddTo.additional.hasOwnProperty('children')){
    throw new Error("Condition must have 'additional.children' property defined.");
  }
  conditionToAddTo.additional.children.push(childCondition);
}

//Simplified API allows omission of '"type":"condition"' by caller when working with conditions. Add this property to condition (and any children) from caller so that Policy API can understand it. Updates the object passed in.
function addTypeConditionToCondition(condition){
  if(condition===undefined || condition === null){
    logger.debug(function(){return "'condition' passed to 'addTypeConditionToCondition' was null or undefined.";});
    throw new Error("Invalid Condition encountered.");
  }
  
  if(!condition.hasOwnProperty('type')){
    condition.type = 'condition';
  }
  if(!condition.hasOwnProperty('additional')){
    throw new Error("Condition and any children it has must have 'additional' property defined.");
  }
  
  //update any child conditions with the type property
  if(condition.additional.hasOwnProperty('children') && condition.additional.children.length > 0){
    for(var childCondition of condition.additional.children){
      addTypeConditionToCondition(childCondition);
    }
  }
  //not condition specifies a condition on it, update with type as appropriate
  if(condition.additional.type==='not'){
    if(condition.additional.hasOwnProperty('condition')){
      addTypeConditionToCondition(condition.additional.condition);
    }
  }
}
//takes in a fully detailed Collection Sequence object (from a get with 'include_children=true') and returns the Rule Condition. The Rule Condition will only be present if an Action exists on the Rule (that Action will refer to the Rule fragment). Returns null if no Rule condition can be found.
function getRuleConditionFromDetailedCollectionSequence(collectionSequenceObject){
  if(!collectionSequenceObject.hasOwnProperty('additional')){
    logger.warn("Collection Sequence passed when trying to retrieve Rule condition has no 'additional' property.");
    return null;
  }
  if(!collectionSequenceObject.additional.hasOwnProperty('condition_fragments')){
    logger.warn("Collection Sequence passed when trying to retrieve Rule condition has no 'additional.condition_fragments' property.");
    return null;
  }
  if(collectionSequenceObject.additional.condition_fragments.length===0){
    return null;
  }
  var collectionSequenceId = collectionSequenceObject.id;
  var ruleNotesValue = getRuleNotesValue(collectionSequenceId);
  for(var conditionFragment of collectionSequenceObject.additional.condition_fragments){
    if(conditionFragment.additional.notes===ruleNotesValue){
      return conditionFragment;
    }
  }
  return null;
}

//checks the structure of a condition object to ensure it is a boolean condition with the 'children' property. Returns false if this does not meet criteria 
function checkConditionHasChildrenProperty(conditionToCheck){
  if(conditionToCheck===null){
    logger.error("Policy condition passed is null");
    return false;
  }
  if(!conditionToCheck.hasOwnProperty('additional')){
    logger.error("Policy condition passed has no 'additional' property: "+JSON.stringify(conditionToCheck));
    return false;
  }
  if(conditionToCheck.additional.type !== 'boolean'){
    logger.error("Policy condition passed is not of type 'boolean': "+JSON.stringify(conditionToCheck));
    return false;
  }
  if(!conditionToCheck.additional.hasOwnProperty('children')){
    logger.error("Policy condition passed has no 'additional.children' property: "+JSON.stringify(conditionToCheck));
    return false;
  }
  return true;
}

//takes in an Action Root condition and looks in its children for a Condition matching the ID passed in. Returns null if condition not found otherwise returns the matching condition.
function getConditionByIdFromActionRootCondition(conditionObject, idToFind){
  logger.debug(function(){return "Checking for condition ID: "+idToFind+" on Action Root Condition ID: "+conditionObject.id;});
  return getConditionByIdFromBooleanCondition(conditionObject, idToFind);
}

//checks a boolean condition and its children for condition that matches ID specified
function getConditionByIdFromBooleanCondition(conditionObject, idToFind){
  var isValid = checkConditionHasChildrenProperty(conditionObject);
  if(!isValid){
    logger.error("Policy Boolean condition passed to check for ID of condition was not valid.");
    throw "Unable to retrieve condition.";
  }
  
  if(conditionObject.id===idToFind){
      return conditionObject;
    } 
  
  if(conditionObject.additional.children===null){
    logger.warn("Policy condition passed to check for ID of condition has 'children' set to null.");
    return null;
  }
  
  for(var childCondition of conditionObject.additional.children){
    if(childCondition === null || childCondition.additional===undefined || childCondition.additional===null){
      logger.warn(function(){return "Child condition is not valid. It will be ignored. Root condition: "+JSON.stringify(conditionObject);});
      continue;
    }
    //Rule fragment condition should be ignored
    if(childCondition.additional.notes === conditionValues.ACTION_RULE_FRAGMENT){
      continue;
    }
    if(childCondition.id===idToFind){
      return childCondition;
    }     
    //if this is a boolean condition check its children also
    if(childCondition.additional.type === 'boolean'){
      var nestedConditionsResult = getConditionByIdFromBooleanCondition(childCondition, idToFind);
      if(nestedConditionsResult!==null){
        return nestedConditionsResult;
      }
    }
    //if this is a not condition check if the ID is for a condition it negates
    if(childCondition.additional.type === 'not'){
      var notConditionResult = getConditionByIdFromNotCondition(childCondition, idToFind);
      if(notConditionResult!==null){
        return notConditionResult;
      }
    }     
  }  
  return null;
}

//checks if a not condition matches the specified ID or if its negated condition matches the specified ID.
//If the negated condition has further conditions e.g. is a Boolean or another Not condition, those IDs will also be checked.
function getConditionByIdFromNotCondition(notConditionObject, idToFind){
  if(notConditionObject.id===idToFind){
    return notConditionObject;
  }  
  var negatedCondition = notConditionObject.additional.condition;
  if(negatedCondition===null || negatedCondition===undefined){
    return null;
  }
  if(negatedCondition.id===idToFind){
    return negatedCondition;
  }
  if(negatedCondition.additional.type === 'boolean'){
    var nestedNegatedBooleanConditionResult = getConditionByIdFromBooleanCondition(negatedCondition, idToFind);
    if(nestedNegatedBooleanConditionResult!==null){
      return nestedNegatedBooleanConditionResult;
    }
  }
  if(negatedCondition.additional.type === 'not'){
    var nestedNegatedNotConditionResult = getConditionByIdFromNotCondition(negatedCondition, idToFind);
    if(nestedNegatedNotConditionResult!==null){
      return nestedNegatedNotConditionResult;
    }
  }
  return null;
}

//takes in a Rule Root condition and looks in its children for a Condition matching the ID passed in. Returns null if condition not found otherwise returns the matching condition.
function getConditionByIdFromRuleRootCondition(conditionObject, idToFind){
  logger.debug(function(){return "Checking for condition ID: "+idToFind+" on Rule Root Condition ID: "+conditionObject.id;});
  return getConditionByIdFromBooleanCondition(conditionObject, idToFind);
}