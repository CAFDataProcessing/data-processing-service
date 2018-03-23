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

var Q = require('q');
var logger = require('../../helpers/loggingHelper.js');
var tenantConfigStore = {};
var apiErrorFactory = require('../errors/apiErrorFactory.js');

module.exports = {
    setTenantConfig: setTenantConfig,
    deleteTenantConfig: deleteTenantConfig,
    getTenantConfig: getTenantConfig,
    getTenantConfigs: getTenantConfigs
};

function setTenantConfig(tenantId, key, value) {
    var setPromise = Q.defer();
    var config = {};
    config[key] = value;
    if (tenantConfigStore[tenantId] === undefined) {
        tenantConfigStore[tenantId] = [config];
        setPromise.resolve();
    } else {
        for (var i = 0; i < tenantConfigStore[tenantId].length; i++) {
            for (var configKey in tenantConfigStore[tenantId][i]) {
                if (configKey === key) {
                    tenantConfigStore[tenantId][i][configKey] = value;
                    setPromise.resolve();
                    return setPromise.promise;
                }
            }
        }
        tenantConfigStore[tenantId].push({[key]: value});
        setPromise.resolve();
    }
    return setPromise.promise;
}

function getTenantConfig(tenantId, key) {
    var getPromise = Q.defer();
    var value;
    if (tenantConfigStore[tenantId] === undefined) {
        getPromise.reject(apiErrorFactory.createNotFoundError("No configuration found for this tenant and key."));
        return getPromise.promise;
    }
    for (var i = 0; i < tenantConfigStore[tenantId].length; i++) {
        for (var config in tenantConfigStore[tenantId][i]) {
            if (config === key) {
                value = tenantConfigStore[tenantId][i][config];
            }
        }
    }
    if (value !== undefined) {
        getPromise.resolve(value);
    } else {
        getPromise.reject(apiErrorFactory.createNotFoundError("No configuration found for this tenant and key."));
    }
    return getPromise.promise;
}

function deleteTenantConfig(tenantId, key) {
    var deletePromise = Q.defer();

    if (tenantConfigStore[tenantId] === undefined) {
        deletePromise.reject(apiErrorFactory.createNotFoundError("No configuration found for this tenant."));
        return deletePromise.promise;
    }

    for (var i = 0; i < tenantConfigStore[tenantId].length; i++) {
        for (var configKey in tenantConfigStore[tenantId][i]) {
            if (configKey === key) {
                if (tenantConfigStore[tenantId].length === 1) {
                    tenantConfigStore[tenantId] = undefined;
                } else {
                    tenantConfigStore[tenantId].splice(i, 1);
                }
                deletePromise.resolve();
                return deletePromise.promise;
            }
        }
    }
    deletePromise.reject(apiErrorFactory.createNotFoundError("No configuration found for tenant " + tenantId + " with key " + key));
    return deletePromise.promise;
}

function getTenantConfigs(tenantId) {
    var getPromise = Q.defer();

    if (tenantConfigStore[tenantId] === undefined || tenantConfigStore[tenantId].length === 0) {
        getPromise.resolve([]);
    } else {
        getPromise.resolve(tenantConfigStore[tenantId]);
    }
    return getPromise.promise;
}