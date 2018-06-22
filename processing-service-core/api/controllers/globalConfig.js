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
var globalConfigModel = require('../models/globalConfig.js');
var logger = require('../helpers/loggingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');

module.exports = {
  setGlobalConfig: setGlobalConfig,
  deleteGlobalConfig: deleteGlobalConfig,
  getGlobalConfig: getGlobalConfig,
  getGlobalConfigs: getGlobalConfigs
};

function setGlobalConfig(req, res, next) {
  var globalConfigParams = {
    key: req.swagger.params['key'].value,
    default: req.swagger.params.globalConfig.value.default,
    description: req.swagger.params.globalConfig.value.description
  };
  logger.info('Creating global configuration using parameters: '+ JSON.stringify(globalConfigParams));
  var setPromise = globalConfigModel.setGlobalConfig(globalConfigParams);
  httpHelper.writeUpdatePromiseJSONResultToResponse(setPromise, res);
}

function getGlobalConfig(req, res, next) {
  var key = req.swagger.params['key'].value;
  logger.info('Retrieving global configuration using parameters: '+ key);
  var getPromise = globalConfigModel.getGlobalConfig(key);
  httpHelper.writePromiseJSONResultToResponse(getPromise, res);
}

function getGlobalConfigs(req, res, next) {
  logger.info('Retrieving global configurations');
  var getPromise = globalConfigModel.getGlobalConfigs();
  httpHelper.writePromiseJSONResultToResponse(getPromise, res);
};

function deleteGlobalConfig(req, res, next) {
  var key = req.swagger.params['key'].value;
  logger.info('Deleting global configuration using parameters: '+ key);
  var deletePromise = globalConfigModel.deleteGlobalConfig(key);
  httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, res);
}
