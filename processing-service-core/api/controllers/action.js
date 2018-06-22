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
var actionModel = require('../models/action.js');
var logger = require('../helpers/loggingHelper.js');
var errorCallbackHelper = require('../helpers/errorCallbackHelper.js');
var errorResponseHelper = require('../models/errorResponse.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var projectIdProvider = require('../libs/projectIdProvider.js');

module.exports = {
  createAction: createAction,
  deleteAction: deleteAction,
  getAction: getAction,
  getActions: getActions,
  updateAction: updateAction
};

function createAction(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var createActionParams = {
    description: req.swagger.params.newAction.value.description,
    name: req.swagger.params.newAction.value.name,
    order: req.swagger.params.newAction.value.order,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,    
    settings: req.swagger.params.newAction.value.settings,    
    typeId: req.swagger.params.newAction.value.typeId,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info('Creating action using parameters: '+ JSON.stringify(createActionParams));
  var createPromise = actionModel.createAction(createActionParams)
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function deleteAction(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var deleteActionParams = {
    id: req.swagger.params.id.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info('Deleting action using parameters: '+ JSON.stringify(deleteActionParams));
  var deletePromise = actionModel.deleteAction(deleteActionParams);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getAction(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getActionParams = {
    id: req.swagger.params.id.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info('Retrieving action using parameters: '+ JSON.stringify(getActionParams));
  var getPromise = actionModel.getAction(getActionParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getActions(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getActionsParams = {
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info('Retrieving actions using parameters: '+ JSON.stringify(getActionsParams));
  var getPromise = actionModel.getActions(getActionsParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function updateAction(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var updateActionParams = {
    description: req.swagger.params.updatedAction.value.description,
    id: req.swagger.params.id.value,
    name: req.swagger.params.updatedAction.value.name,
    order: req.swagger.params.updatedAction.value.order,
    project_id: project_id,
    ruleId: req.swagger.params.ruleId.value,
    settings: req.swagger.params.updatedAction.value.settings,
    typeId: req.swagger.params.updatedAction.value.typeId,    
    workflowId: req.swagger.params.workflowId.value
  };
  logger.info('Retrieving action using parameters: '+ JSON.stringify(updateActionParams));
  var updatePromise = actionModel.updateAction(updateActionParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}