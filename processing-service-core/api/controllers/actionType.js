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
var errorCallbackHelper = require('../helpers/errorCallbackHelper.js');
var actionTypeModel = require('../models/actionType.js');
var projectIdProvider = require('../libs/projectIdProvider.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');

module.exports = {
  createActionType: createActionType,
  deleteActionType: deleteActionType,
  getActionType: getActionType,
  getActionTypes: getActionTypes,
  updateActionType: updateActionType
};

function createActionType(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var createActionTypeParams = {
    definition: req.swagger.params.newActionType.value.definition,
    description: req.swagger.params.newActionType.value.description,
    internal_name: req.swagger.params.newActionType.value.internal_name,
    name: req.swagger.params.newActionType.value.name,    
    project_id: project_id
  };
  logger.info('Creating action type using parameters: '+ JSON.stringify(createActionTypeParams));
  var createPromise = actionTypeModel.create(createActionTypeParams);
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function deleteActionType(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var deleteActionTypeParams = {
    id: req.swagger.params.id.value,
    project_id: project_id
  };
  logger.info('Delete action type using parameters: '+ JSON.stringify(deleteActionTypeParams));
  var deletePromise = actionTypeModel.delete(deleteActionTypeParams);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getActionType(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getActionTypeParams = {
    id: req.swagger.params.id.value,
    project_id: project_id
  };  
  logger.info('Get action type using parameters: '+ JSON.stringify(getActionTypeParams));
  var getPromise = actionTypeModel.get(getActionTypeParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getActionTypes(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getActionTypesParams = {
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    project_id: project_id
  };
  logger.info('Retrieving action types using parameters: '+ JSON.stringify(getActionTypesParams));
  var getPromise = actionTypeModel.getActionTypes(getActionTypesParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function updateActionType(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var updateActionTypeParams = {
    definition: req.swagger.params.updatedActionType.value.definition,
    description: req.swagger.params.updatedActionType.value.description,
    id: req.swagger.params.id.value,
    internal_name: req.swagger.params.updatedActionType.value.internal_name,
    name: req.swagger.params.updatedActionType.value.name,    
    project_id: project_id
  };
  logger.info('Update action type using parameters: '+ JSON.stringify(updateActionTypeParams));
  var updatePromise = actionTypeModel.update(updateActionTypeParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}