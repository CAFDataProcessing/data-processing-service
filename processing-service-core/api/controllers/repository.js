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
'use strict';

var logger = require('../helpers/loggingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var repositoryModel = require('../models/repository.js');

module.exports = {
    setRepositoryConfig: setRepositoryConfig,
    setRepositoryConfigs: setRepositoryConfigs,
    deleteRepositoryConfig: deleteRepositoryConfig,
    deleteRepositoryConfigs: deleteRepositoryConfigs,
    getEffectiveRepositoryConfig: getEffectiveRepositoryConfig,
    getEffectiveRepositoryConfigs: getEffectiveRepositoryConfigs,
    getRepositoryConfig: getRepositoryConfig,
    getRepositoryConfigs: getRepositoryConfigs
};

function setRepositoryConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    var key = req.swagger.params.key.value;
    var value = req.swagger.params.value.value;

    logger.info("Set repository specific config called with tenantId " + tenantId + ", repositoryId " + repositoryId + " key " 
            + key + " and value " + value);

    var createPromise = repositoryModel.setRepositoryConfig(tenantId, repositoryId, key, value);
    httpHelper.writeUpdatePromiseJSONResultToResponse(createPromise, response);
}

function setRepositoryConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    var repositoryConfigs = req.swagger.params.repositoryConfigs.value;

    logger.info("Set repository specific config called with tenantId " + tenantId + ", repositoryId " + repositoryId 
            + " and repositoryConfigs: " + repositoryConfigs);

    var createPromise = repositoryModel.setRepositoryConfigs(tenantId, repositoryId, repositoryConfigs);
    httpHelper.writeUpdatePromiseJSONResultToResponse(createPromise, response);
}

function deleteRepositoryConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    var key = req.swagger.params.key.value;

    logger.info("Delete repository specific config called with tenantId: " + tenantId + ", repositoryId " 
            + repositoryId + " and key: " + key);

    var deletePromise = repositoryModel.deleteRepositoryConfig(tenantId, repositoryId, key);
    httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function deleteRepositoryConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    logger.info("Delete repository specific config called with tenantId: " + tenantId + " and repository " + repositoryId);

    var deletePromise = repositoryModel.deleteRepositoryConfigs(tenantId, repositoryId);
    httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getEffectiveRepositoryConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    var key = req.swagger.params.key.value;

    logger.info("Get effective config called with tenantId: " + tenantId + ", repositoryId " + repositoryId + " and key: " + key);

    var effectiveConfigPromise = repositoryModel.getEffectiveRepositoryConfig(tenantId, repositoryId, key);
    httpHelper.writePromiseJSONResultToResponse(effectiveConfigPromise, response);
}

function getEffectiveRepositoryConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    logger.info("Get effective configs called with tenantId: " + tenantId + " and repositoryId: " + repositoryId);

    var effectiveConfigPromise = repositoryModel.getEffectiveRepositoryConfigs(tenantId, repositoryId);
    httpHelper.writePromiseJSONResultToResponse(effectiveConfigPromise, response);
}

function getRepositoryConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    var key = req.swagger.params.key.value;

    logger.info("Get repository specific config called with tenantId: " + tenantId + ", repositoryId: " 
            + repositoryId + " and key: " + key);

    var repositoryConfigPromise = repositoryModel.getRepositoryConfig(tenantId, repositoryId, key);
    httpHelper.writeStringPromiseJSONResultToResponse(repositoryConfigPromise, response);
}

function getRepositoryConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var repositoryId = req.swagger.params.repositoryId.value;
    logger.info("Get repository specific configs called with tenantId: " + tenantId + " and repositoryId: " + repositoryId);

    var repositoryConfigPromise = repositoryModel.getRepositoryConfigs(tenantId, repositoryId);
    httpHelper.writePromiseJSONResultToResponse(repositoryConfigPromise, response);
}
