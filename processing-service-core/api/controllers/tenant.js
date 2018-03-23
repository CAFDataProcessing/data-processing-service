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
'use strict';

var logger = require('../helpers/loggingHelper.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var tenantModel = require('../models/tenant.js');

module.exports = {
    setTenantConfig: setTenantConfig,
    setTenantConfigs: setTenantConfigs,
    deleteTenantConfig: deleteTenantConfig,
    deleteTenantConfigs: deleteTenantConfigs,
    getEffectiveTenantConfig: getEffectiveTenantConfig,
    getEffectiveTenantConfigs: getEffectiveTenantConfigs,
    getTenantConfig: getTenantConfig,
    getTenantConfigs: getTenantConfigs
};

function setTenantConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var key = req.swagger.params.key.value;
    var value = req.swagger.params.value.value;

    logger.info("Set tenant specific config called with tenantId " + tenantId + ", key " + key + " and value " + value);

    var createPromise = tenantModel.setTenantConfig(tenantId, key, value);
    httpHelper.writeUpdatePromiseJSONResultToResponse(createPromise, response);
}

function setTenantConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var tenantConfigs = req.swagger.params.tenantConfigs.value;

    logger.info("Set tenant specific config called with tenantId " + tenantId + " and tenantConfigs: " + tenantConfigs);

    var createPromise = tenantModel.setTenantConfigs(tenantId, tenantConfigs);
    httpHelper.writeUpdatePromiseJSONResultToResponse(createPromise, response);
}

function deleteTenantConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var key = req.swagger.params.key.value;

    logger.info("Delete tenant specific config called with tenantId: " + tenantId + " and key: " + key);

    var deletePromise = tenantModel.deleteTenantConfig(tenantId, key);
    httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function deleteTenantConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;

    logger.info("Delete tenant specific config called with tenantId: " + tenantId);

    var deletePromise = tenantModel.deleteTenantConfigs(req.swagger.params.tenantId.value);
    httpHelper.writeDeletePromiseJSONResultToResponse(deletePromise, response);
}

function getEffectiveTenantConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var key = req.swagger.params.key.value;

    logger.info("Get effective config called with tenantId: " + tenantId + " and key: " + key);

    var effectiveConfigPromise = tenantModel.getEffectiveTenantConfig(tenantId, key);
    httpHelper.writePromiseJSONResultToResponse(effectiveConfigPromise, response);
}

function getEffectiveTenantConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;

    logger.info("Get effective configs called with tenantId: " + tenantId);

    var effectiveConfigPromise = tenantModel.getEffectiveTenantConfigs(tenantId);
    httpHelper.writePromiseJSONResultToResponse(effectiveConfigPromise, response);
}

function getTenantConfig(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;
    var key = req.swagger.params.key.value;

    logger.info("Get tenant specific config called with tenantId: " + tenantId + " and key: " + key);

    var tenantConfigPromise = tenantModel.getTenantConfig(tenantId, key);
    httpHelper.writeStringPromiseJSONResultToResponse(tenantConfigPromise, response);
}

function getTenantConfigs(req, response, next) {
    var tenantId = req.swagger.params.tenantId.value;

    logger.info("Get tenant specific configs called with tenantId: " + tenantId);

    var tenantConfigPromise = tenantModel.getTenantConfigs(tenantId);
    httpHelper.writePromiseJSONResultToResponse(tenantConfigPromise, response);
}
