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

var Q = require('q');
var logger = require('../helpers/loggingHelper.js');
var tenantConfigStoreModel = require("./db/tenantConfigStore.js");
var globalConfigStoreModel = require("./db/globalConfigStore.js");
var apiErrorFactory = require('./errors/apiErrorFactory.js');
var apiErrorTypes = require('./errors/apiErrorTypes.js');
var ApiError = require('./errors/apiError.js');


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

/**
 * Creates or updates tenant specific config.
 * Creates or updates a custom configuration setting for this key against this tenant if one doesn't exist or updates the currently 
 * existing custom configuration setting if it does.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} key String The custom configuration key.
 * @param {String} value String The value to use when setting the custom config for the tenants key. (optional)
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of create/update.
 **/
function setTenantConfig(tenantId, key, value) {
    var tenantConfig = Q.defer();
    logger.debug("Creating tenant specific config for tenant " + tenantId + " with key " + key + " and value " + value);
    tenantConfigStoreModel.setTenantConfig(tenantId, key, value)
            .then(function (createConfigPromise) {
                logger.debug("Configuration of " + key + " set for tenant " + tenantId + " with value " + value);
                tenantConfig.resolve();
            })
            .fail(function (errorResponse) {
                logger.debug("Unable to set configuration for key " + key + " for tenant " + tenantId + " with value " + value);
                if (errorResponse instanceof ApiError && errorResponse.type === apiErrorTypes.ITEM_NOT_FOUND) {
                    tenantConfig.reject(apiErrorFactory.createMethodNotAllowedError("No global configuration was found for this key"));
                }
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Creates or updates tenant specific configs.
 * Creates or updates a custom configuration setting for this key against the tenant.
 *
 * @param {String} tenantId  String The ID of the tenant.
 * @param {String} tenantConfigs TenantConfigs The json string representation key-value pair map of custom tenant configuration.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of create/update.
 **/
function setTenantConfigs(tenantId, tenantConfigs) {
    var tenantConfig = Q.defer();
    var promiseArray = [];

    for (var i = 0; i < tenantConfigs.length; i++) {
        var config = tenantConfigs[i];
        promiseArray.push(tenantConfigStoreModel.setTenantConfig(tenantId, config.key, config.value));
    }
    Q.all(promiseArray)
            .then(function (results) {
                logger.debug("Created tenant specific config for tenant " + tenantId);
                tenantConfig.resolve();
            })
            .fail(function (errorResponse) {
                logger.debug("Unable to create tenant specific config for tenant " + tenantId);
                tenantConfig.reject(errorResponse);
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Delete tenant specific config for the provided key.
 * Deletes tenant specific configuration for the key that has been provided.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of deletion.
 **/
function deleteTenantConfig(tenantId, key) {
    var tenantConfig = Q.defer();
    logger.debug("Deleting tenant specific config for tenant " + tenantId + " with key " + key);
    tenantConfigStoreModel.deleteTenantConfig(tenantId, key)
            .then(function (deleteConfigPromise) {
                logger.debug("Successfully deleted tenant specific config for tenant " + tenantId + " with key " + key);
                tenantConfig.resolve(deleteConfigPromise);
            })
            .fail(function (errorResponse) {
                logger.debug("Failed to delete tenant specific config for tenant " + tenantId + " with key " + key);
                tenantConfig.reject(errorResponse);
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Delete tenant specific configs for the provided tenantId.
 * Deletes all tenant specific configuration for tenant that has been provided.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of deletion.
 **/
function deleteTenantConfigs(tenantId) {
    var tenantConfig = Q.defer();
    var deletionPromises = [];
    tenantConfigStoreModel.getTenantConfigs(tenantId)
            .then(function (tenantConfigs) {
                for (var tenantConfigsIndex = 0; tenantConfigsIndex < tenantConfigs.length; tenantConfigsIndex++) {
                    var tenantConfigEntry = tenantConfigs[tenantConfigsIndex]
                    deletionPromises.push(tenantConfigStoreModel.deleteTenantConfig(tenantId, tenantConfigEntry.key));
                }
                logger.debug("Successfully deleted tenant specific config for tenant " + tenantId);
                return deletionPromises;
            })
            .all()
            .then(function(){
                tenantConfig.resolve();
            })
            .fail(function (errorResponse) {
                logger.debug("Unable to remove tenant specific config for tenant {}", tenantId);
                tenantConfig.reject(errorResponse);
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Retrieve effective tenant specific config.
 * Returns the effective tenant specific config for this key. That can be either a custom value specified for the tenant or the default.
 *
 * @param {String} tenantId String The ID tenant.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getEffectiveTenantConfig(tenantId, key) {
    var tenantConfig = Q.defer();
    var effectiveConfig = {};
    logger.debug("Retrieving effective tenant config for tenant " + tenantId + " with key " + key);
    tenantConfigStoreModel.getTenantConfig(tenantId, key)
            .then(function (tenantConfigResult) {
                effectiveConfig.value = tenantConfigResult;
                effectiveConfig.valueType = "CUSTOM";
                tenantConfig.resolve(effectiveConfig);
            })
            .fail(function (errorResponse) {
                logger.debug("No tenant specific config for tenant " + tenantId + " with key " + key + " was found, returning default"
                        + "value");
                if (errorResponse instanceof ApiError && errorResponse.type === apiErrorTypes.ITEM_NOT_FOUND) {
                    globalConfigStoreModel.getGlobalConfig(key)
                            .then(function (tenantDefaultConfig) {
                                effectiveConfig.value = tenantDefaultConfig.default;
                                effectiveConfig.valueType = "DEFAULT";
                                tenantConfig.resolve(effectiveConfig);
                            })
                            .fail(function (errorResponse) {
                                tenantConfig.reject(errorResponse);
                            })
                            .done();
                } else {
                    tenantConfig.reject(errorResponse);
                }
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Retrieve effective tenant specific config.
 * Returns the effective tenant specific config for this key. That can be either a custom value specified for the tenant or the default.
 *
 * @param {String} tenantId String The ID tenant.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getEffectiveTenantConfigs(tenantId) {

    var tenantConfig = Q.defer();
    var tenantConfigCallComplete = Q.defer();
    logger.debug("Retrieving efective tenant config for tenant " + tenantId);
    var configurations = [];
    tenantConfigStoreModel.getTenantConfigs(tenantId)
            .then(function (tenantConfigurations) {
                for (var tenantConfigurationIndex = 0; tenantConfigurationIndex < tenantConfigurations.length;
                        tenantConfigurationIndex++) {
                    var tenantConfigEntry = tenantConfigurations[tenantConfigurationIndex];
                    var builtTenantConfig = {
                        key: tenantConfigEntry.key,
                        value: tenantConfigEntry.value,
                        valueType: "CUSTOM"
                    };
                    configurations.push(builtTenantConfig);
                }
                tenantConfigCallComplete.resolve();
            })
            .fail(function (errorResponse) {
                logger.debug("Ending processing of request as there has been a problem retrieving the information requested from the"
                        + " store");
                tenantConfigCallComplete.reject();
                tenantConfig.reject(errorResponse);
                return tenantConfig.promise;
            })
            .done();

    /** Using a second promise that doesn't leave this method to ensure that the tenat specific config call is complete before calling 
     * the for global configs.
     */
    tenantConfigCallComplete.promise
            .then(function () {
                return globalConfigStoreModel.getGlobalConfigs();
            })
            .then(function (globalConfigs) {
                for (var globalConfigIndex = 0; globalConfigIndex < globalConfigs.length; globalConfigIndex++) {
                    var globalConfigEntry = globalConfigs[globalConfigIndex];
                    if (containsKey(configurations, globalConfigEntry.key) === false) {
                        var builtEffectiveConfig = {
                            key: globalConfigEntry.key,
                            value: globalConfigEntry.default,
                            valueType: "DEFAULT"
                        };
                        configurations.push(builtEffectiveConfig);
                    }
                }
                tenantConfig.resolve(configurations);
            })
            .fail(function (errorResponse) {
                /** Taking no action if errorResponse is undefined as that will happen in a case where an error occured during the call for
                 tenant specific configs.*/
                if (errorResponse !== undefined) {
                    logger.debug("Unable to retrieve effective tenant config for tenant " + tenantId);
                    tenantConfig.reject(errorResponse);
                }
            })
            .done();

    return tenantConfig.promise;
}

/**
 * Retrieve tenant specific config.
 * Returns the tenant specific config for this key that is stored against this tenant.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getTenantConfig(tenantId, key) {
    var tenantConfig = Q.defer();
    logger.debug("Retrieving tenant specific config for tenant " + tenantId + " with key " + key);
    tenantConfigStoreModel.getTenantConfig(tenantId, key)
            .then(function (tenantConfigResult) {
                tenantConfig.resolve(tenantConfigResult);
            })
            .fail(function (errorResponse) {
                logger.debug("Unable to retrieve tenant specific config for tenant " + tenantId + " with key " + key);
                tenantConfig.reject(errorResponse);
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Retrieve tenant specific configs as map of key-value pairs.
 * Returns all tenant specific configs as a list.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getTenantConfigs(tenantId) {
    var tenantConfig = Q.defer();
    var configurations = [];
    logger.debug("Retrieving tenant specific config for tenant " + tenantId);
    tenantConfigStoreModel.getTenantConfigs(tenantId)
            .then(function (tenantConfigResult) {
                for (var i = 0; i < tenantConfigResult.length; i++) {
                    var tenantConfigEntry = tenantConfigResult[i];
                    var config = {
                        key: tenantConfigEntry.key,
                        value: tenantConfigEntry.value
                    };
                    configurations.push(config);
                }
                tenantConfig.resolve(configurations);
            })
            .fail(function (errorResponse) {
                logger.debug("Unable to retrieve tenant specific config for tenant " + tenantId);
                tenantConfig.reject(errorResponse);
            })
            .done();
    return tenantConfig.promise;
}

/**
 * Convenience method to identify if a config object within an array contains a key matching the key provide.
 * 
 * @param {Array} configArray The array of configuration objects to be used.
 * @param {String} key The key to match against.
 * @returns {Boolean} If an element in the array's key matched or not.
 */
function containsKey(configArray, key) {
    for (var i = 0; i < configArray.length; i++) {
        if (configArray[i].key === key) {
            return true;
        }
    }
    return false;
}
