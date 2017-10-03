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
//holds methods to take Policy API Objects and extract relevant properties for Data Processing API Objects.
var logger = require('./loggingHelper.js');
var conditionModel = require('../models/policy_api/condition.js');

module.exports = {
  buildActionFromCollection: buildActionFromCollection,
  buildActionFromCollectionEntry: buildActionFromCollectionEntry,
  buildActionFromPolicy: buildActionFromPolicy,
  buildActionFromPolicyType: buildActionFromPolicyType,
  buildActionConditionsFromRootActionPolicyCondition: buildActionConditionsFromRootActionPolicyCondition,
  buildActionTypeFromPolicyType: buildActionTypeFromPolicyType,
  buildConditionFromPolicyCondition: buildConditionFromPolicyCondition,
  buildRuleConditionsFromRootRulePolicyCondition: buildRuleConditionsFromRootRulePolicyCondition,
  buildRuleFromCollectionSequence: buildRuleFromCollectionSequence,
  buildRuleFromCollectionSequenceAndOrder: buildRuleFromCollectionSequenceAndOrder,
  buildRuleFromSequenceEntry: buildRuleFromSequenceEntry,
  buildWorkflowFromPolicyWorkflow: buildWorkflowFromPolicyWorkflow
};

function buildActionTypeFromPolicyType(policyType, actionType){
  var actionTypeToReturn = actionType !== null && actionType !== undefined ? actionType : {};
  actionTypeToReturn.id = policyType.id;
  actionTypeToReturn.name = policyType.name;
  actionTypeToReturn.description = policyType.description;
  actionTypeToReturn.internal_name = policyType.additional.short_name;
  actionTypeToReturn.definition = policyType.additional.definition;
  return actionTypeToReturn;
};

function buildWorkflowFromPolicyWorkflow(polWorkflow, dWorkflow){
  var workflowToReturn = dWorkflow !== null && dWorkflow !== undefined ? dWorkflow : {};
  workflowToReturn.description = polWorkflow.description;
  workflowToReturn.id = polWorkflow.id;
  workflowToReturn.notes = polWorkflow.additional.notes;
  workflowToReturn.name = polWorkflow.name;
  return workflowToReturn;
}

//expects to be passed in the Action Root Condition as returned by Policy API, with its children set. Returns the conditions that are a child of this Root except for the fragment condition pointing to the Rule.
function buildActionConditionsFromRootActionPolicyCondition(policyCondition){
  var isValid = conditionModel.checkConditionHasChildrenProperty(policyCondition);
  if(!isValid){
    throw "Unable to construct Conditions for Action.";
  }
  var actionConditions = [];
  if(policyCondition.additional.children===null){
    logger.debug("Policy condition passed to construct Action conditions from has children set to 'null'. May not have been retrieved using 'include_children=true'. Rule Condition ID: "+policyCondition.id);
    return actionConditions;
  }
  //don't expose the root level Action condition in Processing API. It is an internal concept.  
  for(var childCondition of policyCondition.additional.children){
    //don't return the fragment condition that points to Rule condition.
    if(childCondition.additional.notes===conditionModel.conditionValues.ACTION_RULE_FRAGMENT){
      continue;
    }    
    actionConditions.push(buildConditionFromPolicyCondition(childCondition));
  }
  return actionConditions;  
}

//expects to be passed in the Rule Root Condition as returned by Policy API and returns the Conditions set as children of this Root in the form of Processing API Conditions.
function buildRuleConditionsFromRootRulePolicyCondition(policyCondition){
  var isValid = conditionModel.checkConditionHasChildrenProperty(policyCondition);
  if(!isValid){
    throw "Unable to construct Conditions for Rule.";
  }
  var ruleConditions = [];
  if(policyCondition.additional.children===null){
    logger.debug("Policy condition passed to construct Rule conditions from has children set to 'null'. May not have been retrieved using 'include_children=true'. Rule Condition ID: "+policyCondition.id);
    return ruleConditions;
  }
  //don't expose the root level Rule condition in Processing API. It is an internal concept.  
  for(var childCondition of policyCondition.additional.children){
    ruleConditions.push(buildConditionFromPolicyCondition(childCondition));
  }
  return ruleConditions;  
}

function buildConditionFromPolicyCondition(policyCondition, condition){
  var conditionToReturn = condition !== null && condition !== undefined ? condition : {};
  conditionToReturn.id = policyCondition.id;
  conditionToReturn.name = policyCondition.name;
  conditionToReturn.additional = {};
  conditionToReturn.additional.order = policyCondition.additional.order;
  conditionToReturn.additional.notes = policyCondition.additional.notes;
  conditionToReturn.additional.type = policyCondition.additional.type;
  
  //for some types we can reduce the properties returned to those relevant to this API.
  switch(policyCondition.additional.type){
    case 'boolean':
      buildBooleanAdditionalFromPolicyBooleanAdditional(policyCondition.additional, conditionToReturn.additional);
      break;
    case 'regex':
      buildRegexAdditionalFromPolicyRegexAddtional(policyCondition.additional, conditionToReturn.additional);
      break;
    case 'date':
      buildGenericComparisonConditionFromPolicyAdditional(policyCondition.additional, conditionToReturn.additional, 'date');
      break;
    case 'number':
      buildGenericComparisonConditionFromPolicyAdditional(policyCondition.additional, conditionToReturn.additional, 'number');
      break;
    case 'string':
      buildGenericComparisonConditionFromPolicyAdditional(policyCondition.additional, conditionToReturn.additional, 'string');
      break;
    case 'exists':
      buildExistsAdditionalFromPolicyExistsAdditional(policyCondition.additional, conditionToReturn.additional);
      break;
    case 'fragment':
    case 'lexicon':
    case 'not':
    case 'text':
      //TODO these types aren't yet defined in the model in a simplified form so returning them in complete form for now
      conditionToReturn.additional = policyCondition.additional;
      break;
    default:
      logger.error("Did not recognize type of condition on Policy API Condition when converting to Processing API Condition. Condition was: "+JSON.stringify(policyCondition));
      throw "Unrecognized Condition type returned.";
  }
  return conditionToReturn;  
}

function buildExistsAdditionalFromPolicyExistsAdditional(policyApi, processing){
  processing.type = 'exists';
  processing.field = policyApi.field;
}

//can be used across Condition types that record a value, operator and field.
function buildGenericComparisonConditionFromPolicyAdditional(policyApi, processing, conditionType){
  processing.type = conditionType;
  processing.value = policyApi.value;
  processing.operator = policyApi.operator;
  processing.field = policyApi.field;
  return processing;
}

function buildBooleanAdditionalFromPolicyBooleanAdditional(policyApi, processing){
  processing.type = 'boolean';
  processing.operator = policyApi.operator;
  processing.children = [];
  if(policyApi.children === null || policyApi.children.length ===0){
    return;
  }  
  for(var policyApiChildCondition of policyApi.children){
    //add the child conditions to the processing representation
    processing.children.push(buildConditionFromPolicyCondition(policyApiChildCondition));
  }
  return processing;
}

function buildRegexAdditionalFromPolicyRegexAddtional(policyApi, processing){
  processing.type = 'regex';
  processing.value = policyApi.value;
  processing.field = policyApi.field;
  return processing;
}

//convenience method to take relevant properties from a collection and add them to an object representing an Action. If no action parameter passed then constructs new object.
function buildActionFromCollection(collection, action){
  var actionToReturn = action !== null && action !== undefined ? action : {};
  actionToReturn.id = collection.id;
  actionToReturn.name = collection.name;
  actionToReturn.description = collection.description;
  return actionToReturn;
}
//convenience method to take relevant properties from a policy and add them to an object representing an Action. If no action parameter passed then constructs new object.
function buildActionFromPolicy(policy, action){
  var actionToReturn = action !== null && action !== undefined ? action : {};
  actionToReturn.settings = policy.additional.details;
  actionToReturn.typeId = policy.additional.policy_type_id;
  return actionToReturn;
}
//convenience method to take relevant properties from a policy type and add them to an object representing an Action. If no action parameter passed then constructs new object.
function buildActionFromPolicyType(policyType, action){
  var actionToReturn = action !== null && action !== undefined ? action : {};
  actionToReturn.typeInternalName = policyType.additional.short_name;
  return actionToReturn;
}
//convenience method to take relevant properties from a collection sequence entry on a collection sequence and add them to an object representing an Action. If no action parameter passed then constructs a new object.
function buildActionFromCollectionEntry(collectionEntry, action){
  var actionToReturn = action !== null && action !== undefined ? action : {};
  //expecting Action to only have one collection specified per entry 
  actionToReturn.id = collectionEntry.collection_ids[0];
  actionToReturn.order = collectionEntry.order;
  return actionToReturn;
}

//updates a rule object with relevant properties from passed in sequence entry. Creates new object if no rule is passed
function buildRuleFromSequenceEntry(sequenceEntry, ruleToUpdate){
  var updatedRule = ruleToUpdate !== null && ruleToUpdate !== undefined ? ruleToUpdate : {};
  updatedRule.priority = sequenceEntry.additional.order;
  return updatedRule;
}

//updates a rule object with the relevant properties of passed in collection sequence and the passed in order value. Creates new object if no rule is passed.
function buildRuleFromCollectionSequenceAndOrder(collectionSequenceObject, order, ruleToUpdate){
  var ruleToReturn = buildRuleFromCollectionSequence(collectionSequenceObject, ruleToUpdate);
  ruleToReturn.priority = order;
  return ruleToReturn;
}

//updates a rule object with the relevant properties of passed in collection sequence. Creates new object if no rule is passed.
function buildRuleFromCollectionSequence(collectionSequenceObject, ruleToUpdate){
  var ruleToReturn = ruleToUpdate !== null && ruleToUpdate !== undefined ? ruleToUpdate : {};
  ruleToReturn.id = collectionSequenceObject.id;
  ruleToReturn.name = collectionSequenceObject.name;
  ruleToReturn.description = collectionSequenceObject.description;
  if(collectionSequenceObject.additional===undefined || collectionSequenceObject.additional===null){
    logger.error("Collection Sequence passed to build Rule from has no 'additional' property. Collection Sequence: "+ JSON.stringify(collectionSequenceObject));
    throw "Unable to return Rule.";
  }
  ruleToReturn.enabled = collectionSequenceObject.additional.evaluation_enabled;
  return ruleToReturn;
}
