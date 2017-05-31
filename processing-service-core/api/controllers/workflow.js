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
//controller for handling requests made to the API relating specifically to Workflow objects.
var logger = require('../helpers/loggingHelper.js');
var workflowModel = require('../models/workflow.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var projectIdProvider = require('../libs/projectIdProvider.js');

module.exports = {
  createWorkflow: createWorkflow,
  deleteWorkflow: deleteWorkflow,
  getWorkflow: getWorkflow,
  getWorkflows: getWorkflows,
  updateWorkflow: updateWorkflow
};

function createWorkflow(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var createWorkflowParams = {
    description: req.swagger.params.newWorkflow.value.description,
    name: req.swagger.params.newWorkflow.value.name,
    notes: req.swagger.params.newWorkflow.value.notes,
    project_id: project_id
  };
  logger.info('Creating Workflow using parameters: '+ JSON.stringify(createWorkflowParams));
  var createPromise = workflowModel.create(createWorkflowParams);
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function updateWorkflow(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var updateWorkflowParams = {
    description: req.swagger.params.updatedWorkflow.value.description,
    id: req.swagger.params.id.value,
    name: req.swagger.params.updatedWorkflow.value.name,
    notes: req.swagger.params.updatedWorkflow.value.notes,
    project_id: project_id
  };
  logger.info('Updating Workflow using parameters: '+ JSON.stringify(updateWorkflowParams));
  var updatePromise = workflowModel.update(updateWorkflowParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}

function getWorkflow(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var getWorkflowParams = {
    id: req.swagger.params.id.value,
    project_id: project_id
  };    
  logger.info('Retrieving Workflow using parameters: '+ JSON.stringify(getWorkflowParams));
  var getPromise = workflowModel.get(getWorkflowParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getWorkflows(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getWorkflowsParams = {
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    project_id: project_id
  };
  logger.info('Retrieving Workflows using parameters: '+ JSON.stringify(getWorkflowsParams));
  var getPromise = workflowModel.getWorkflows(getWorkflowsParams)
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function deleteWorkflow(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  
  var deleteWorkflowParams = {
    id: req.swagger.params.id.value,
    project_id: project_id
  };
  logger.info('Deleting Workflow using parameters: '+ JSON.stringify(deleteWorkflowParams));
  var deletePromise = workflowModel.delete(deleteWorkflowParams);  
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}