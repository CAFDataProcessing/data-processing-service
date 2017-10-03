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
var processingRuleModel = require('../models/processingRule.js');
var logger = require('../helpers/loggingHelper.js');
var errorResponseHelper = require('../models/errorResponse.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var projectIdProvider = require('../libs/projectIdProvider.js');

module.exports = {
  createRule: createRule,
  deleteRule: deleteRule,
  getRule: getRule,
  getRules: getRules,
  updateRule: updateRule
};

function getRules(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);
  var getRulesParams = {
    pageNum: req.swagger.params.pageNum.value,
    pageSize: req.swagger.params.pageSize.value,
    workflowId: req.swagger.params.workflowId.value,
    project_id: project_id
  };
  logger.info('Retrieving rules using parameters: '+ JSON.stringify(getRulesParams));
  var getPromise = processingRuleModel.getRules(getRulesParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function getRule(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);

  var getRuleParams = {
    id: req.swagger.params.id.value, 
    project_id: project_id,
    workflowId: req.swagger.params.workflowId.value
  }; 
  logger.info('Retrieving rule using parameters: '+ JSON.stringify(getRuleParams));
  var getPromise = processingRuleModel.getRule(getRuleParams);
  httpHelper.writePromiseJSONResultToResponse(getPromise, response);
}

function updateRule(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);

  var updateRuleParams = {
    id: req.swagger.params.id.value,
    name: req.swagger.params.rule.value.name,
    description: req.swagger.params.rule.value.description,
    enabled: req.swagger.params.rule.value.enabled,
    priority: req.swagger.params.rule.value.priority,
    project_id: project_id,
    workflowId: req.swagger.params.workflowId.value
  };

  logger.info('Updating Rule with parameters: '+ JSON.stringify(updateRuleParams));
  var updatePromise = processingRuleModel.updateRule(updateRuleParams)
  httpHelper.writeUpdatePromiseJSONResultToResponse(updatePromise, response);
}

function createRule(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);

  var createRuleParams = {
    name: req.swagger.params.newRule.value.name,
    description: req.swagger.params.newRule.value.description,
    enabled: req.swagger.params.newRule.value.enabled,
    priority: req.swagger.params.newRule.value.priority,
    project_id: project_id,
    workflowId: req.swagger.params.workflowId.value
  };

  logger.info('Creating Rule with parameters: '+ JSON.stringify(createRuleParams));
  var createPromise = processingRuleModel.createRule(createRuleParams)
  httpHelper.writeCreatePromiseJSONResultToResponse(createPromise, response);
}

function deleteRule(req, response, next){
  var project_id = projectIdProvider.getProjectId(null, req);

  var deleteRuleParams = {
    id: req.swagger.params.id.value,
    project_id: project_id,
    workflowId: req.swagger.params.workflowId.value
  };

  logger.info('Deleting Rule with parameters: '+ JSON.stringify(deleteRuleParams));
  var deletePromise = processingRuleModel.deleteRule(deleteRuleParams);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}