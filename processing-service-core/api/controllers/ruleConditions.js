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
var logger = require('../helpers/loggingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var ruleConditionModel = require('../models/ruleCondition.js');
var projectIdProvider = require('../libs/projectIdProvider.js');

module.exports = {
  createRuleCondition: createRuleCondition,
  deleteRuleCondition: deleteRuleCondition,
  getRuleCondition: getRuleCondition,
  getRuleConditions: getRuleConditions,
  updateRuleCondition: updateRuleCondition
};

function createRuleCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var createRuleConditionParams = {
    condition: req.swagger.params.newCondition.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Creating Rule Condition using parameters: '+ JSON.stringify(createRuleConditionParams);});
  var createPromise = ruleConditionModel.create(createRuleConditionParams);
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function deleteRuleCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var deleteParams = {
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Delete Rule Condition using parameters: '+ JSON.stringify(deleteParams);});
  var deletePromise = ruleConditionModel.delete(deleteParams);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getRuleCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getRuleConditionParams = {
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };  
  logger.info(function(){return 'Retrieving Rule Condition using parameters: '+ JSON.stringify(getRuleConditionParams);});
  var getPromise = ruleConditionModel.get(getRuleConditionParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getRuleConditions(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getRuleConditionsParams = {
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Retrieving Rule Conditions using parameters: '+ JSON.stringify(getRuleConditionsParams);});
  var getPromise = ruleConditionModel.getConditions(getRuleConditionsParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function updateRuleCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var updateParams = {
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    updatedRuleCondition: req.swagger.params.updatedRuleCondition.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Update Rule Condition using parameters: '+ JSON.stringify(updateParams);});
  var updatePromise = ruleConditionModel.update(updateParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}