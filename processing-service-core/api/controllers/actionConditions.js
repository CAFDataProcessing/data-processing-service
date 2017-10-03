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
var logger = require('../helpers/loggingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var actionConditionModel = require('../models/actionCondition.js');
var projectIdProvider = require('../libs/projectIdProvider.js');

module.exports = {
  createActionCondition: createActionCondition,
  deleteActionCondition: deleteActionCondition,
  getActionCondition: getActionCondition,
  getActionConditions: getActionConditions,
  updateActionCondition: updateActionCondition
};

function createActionCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var createParams = {
    actionId: req.swagger.params.actionId.value,
    condition: req.swagger.params.newCondition.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Creating Action Condition using parameters: '+ JSON.stringify(createParams);});
  var createPromise = actionConditionModel.create(createParams);
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function deleteActionCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var deleteParams = {
    actionId: req.swagger.params.actionId.value,
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Deleting Action Condition using parameters: '+ JSON.stringify(deleteParams);});
  var deletePromise = actionConditionModel.delete(deleteParams);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getActionCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getParams = {
    actionId: req.swagger.params.actionId.value,
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  }
  logger.info(function(){return 'Get Action Condition using parameters: '+ JSON.stringify(getParams);});
  var getPromise = actionConditionModel.get(getParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getActionConditions(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getActionConditionsParams = {
    actionId: req.swagger.params.actionId.value,
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Retrieving Action Conditions using parameters: '+ JSON.stringify(getActionConditionsParams);});
  var getPromise = actionConditionModel.getConditions(getActionConditionsParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function updateActionCondition(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var updateParams = {
    actionId: req.swagger.params.actionId.value,
    conditionId: req.swagger.params.conditionId.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    updatedActionCondition: req.swagger.params.updatedActionCondition.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info(function(){return 'Update Action Condition using parameters: '+ JSON.stringify(updateParams);});
  var updatePromise = actionConditionModel.update(updateParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}