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
var repositoryConfigStoreModel = require("./db/repositoryConfigStore.js");
var tenantConfigStoreModel = require("./db/tenantConfigStore.js");
var globalConfigStoreModel = require("./db/globalConfigStore.js");
var apiErrorFactory = require('./errors/apiErrorFactory.js');
var apiErrorTypes = require('./errors/apiErrorTypes.js');
var ApiError = require('./errors/apiError.js');


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

/**
 * Creates or updates repository specific config.
 * Creates or updates a custom configuration setting for this key against this repository if one doesn't exist or updates the currently 
 * existing custom configuration setting if it does.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @param {String} key String The custom configuration key.
 * @param {String} value String The value to use when setting the custom config for the repository key. (optional)
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of create/update.
 **/
function setRepositoryConfig(tenantId, repositoryId, key, value) {
    var repositoryConfig = Q.defer();
    logger.debug("Creating repository specific config for tenant " + tenantId + ", repository " + repositoryId + " with key " + key
        + " and value " + value);
    repositoryConfigStoreModel.setRepositoryConfig(tenantId, repositoryId, key, value)
        .then(function (createConfigPromise) {
            logger.debug("Configuration of " + key + " set for tenant " + tenantId + " for repository " + repositoryId + " with value "
                + value);
            repositoryConfig.resolve();
        })
        .fail(function (errorResponse) {
            logger.debug("Unable to set configuration for key " + key + " for tenant " + tenantId + " for repository " + repositoryId
                + " with value " + value);
            if (errorResponse instanceof ApiError && errorResponse.type === apiErrorTypes.ITEM_NOT_FOUND) {
                repositoryConfig.reject(apiErrorFactory.createMethodNotAllowedError("No global configuration was found for this key"));
            }
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Creates or updates repository specific configs.
 * Creates or updates a custom configuration setting for this key against the tenant/repository.
 *
 * @param {String} tenantId  String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @param {String} repositoryConfigs RepositoryConfigs The json string representation key-value pair map of custom repository 
 * configuration.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of create/update.
 **/
function setRepositoryConfigs(tenantId, repositoryId, repositoryConfigs) {
    var repositoryConfig = Q.defer();
    var promiseArray = [];

    for (var i = 0; i < repositoryConfigs.length; i++) {
        var config = repositoryConfigs[i];
        promiseArray.push(repositoryConfigStoreModel.setRepositoryConfig(tenantId, repositoryId, config.key, config.value));
    }
    Q.all(promiseArray)
        .then(function (results) {
            logger.debug("Created repository specific config for tenant " + tenantId + " and repository " + repositoryId);
            repositoryConfig.resolve();
        })
        .fail(function (errorResponse) {
            logger.debug("Unable to create tenant specific config for tenant " + tenantId + " and repository " + repositoryId);
            repositoryConfig.reject(errorResponse);
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Delete repository specific config for the provided key.
 * Deletes repository specific configuration for the key that has been provided.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of deletion.
 **/
function deleteRepositoryConfig(tenantId, repositoryId, key) {
    var repositoryConfig = Q.defer();
    logger.debug("Deleting repository specific config for tenant " + tenantId + " and repository " + repositoryId + " with key " + key);
    repositoryConfigStoreModel.deleteRepositoryConfig(tenantId, repositoryId, key)
        .then(function (deleteConfigPromise) {
            logger.debug("Successfully deleted repository specific config for tenant " + tenantId + ", repository " + repositoryId
                + " with key " + key);
            repositoryConfig.resolve(deleteConfigPromise);
        })
        .fail(function (errorResponse) {
            logger.debug("Failed to delete repository specific config for tenant " + tenantId + ", repository " + repositoryId
                + " with key " + key);
            repositoryConfig.reject(errorResponse);
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Delete repository specific configs for the provided tenantId and repository.
 * Deletes all repository specific configuration for the tenant and repository that have been provided.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of deletion.
 **/
function deleteRepositoryConfigs(tenantId, repositoryId) {
    var repositoryConfig = Q.defer();
    var deletionPromises = [];
    repositoryConfigStoreModel.getRepositoryConfigs(tenantId, repositoryId)
        .then(function (repositoryConfigs) {
            for (var repositoryConfigsIndex = 0; repositoryConfigsIndex < repositoryConfigs.length; repositoryConfigsIndex++) {
                var repositoryConfigEntry = repositoryConfigs[repositoryConfigsIndex];
                deletionPromises.push(repositoryConfigStoreModel.deleteRepositoryConfig(tenantId, repositoryId,
                    repositoryConfigEntry.key));
            }
            logger.debug("Successfully deleted repository specific config for tenant " + tenantId + " and repository " + repositoryId);
            return deletionPromises;
        })
        .all()
        .then(function () {
            repositoryConfig.resolve();
        })
        .fail(function (errorResponse) {
            logger.debug("Unable to remove repository specific config for tenant " + tenantId + " and repositoryId " + repositoryId);
            repositoryConfig.reject(errorResponse);
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Retrieve effective repository specific config.
 * Returns the effective repository specific config for this key. That can be either a custom value specified for the repository, or the 
 * one specific for the tenant, or the default.
 *
 * @param {String} tenantId String The ID tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getEffectiveRepositoryConfig(tenantId, repositoryId, key) {
    var repositoryConfig = Q.defer();
    var effectiveConfig = {};
    logger.debug("Retrieving effective repository config for tenant " + tenantId + ", and repository " + repositoryId
        + " with key " + key);
    repositoryConfigStoreModel.getRepositoryConfig(tenantId, repositoryId, key)
        .then(function (repositoryConfigResult) {
            effectiveConfig.value = repositoryConfigResult;
            effectiveConfig.valueType = "CUSTOM";
            repositoryConfig.resolve(effectiveConfig);
        })
        .fail(function (errorResponse) {
            logger.debug("No repository specific config for tenant " + tenantId + ", and repository " + repositoryId + " with key "
                + key + " was found, I am going to try with a tenant specific one"
                + "value");
            if (errorResponse instanceof ApiError && errorResponse.type === apiErrorTypes.ITEM_NOT_FOUND) {
                tenantConfigStoreModel.getTenantConfig(tenantId, key)
                    .then(function (tenantConfigResult) {
                        effectiveConfig.value = tenantConfigResult;
                        effectiveConfig.valueType = "CUSTOM";
                        repositoryConfig.resolve(effectiveConfig);
                    })
                    .fail(function (errorResponse) {
                        logger.debug("No tenant specific config for tenant " + tenantId + " with key " + key
                            + " was found, returning default value");
                        if (errorResponse instanceof ApiError && errorResponse.type === apiErrorTypes.ITEM_NOT_FOUND) {
                            globalConfigStoreModel.getGlobalConfig(key)
                                .then(function (tenantDefaultConfig) {
                                    effectiveConfig.value = tenantDefaultConfig.default;
                                    effectiveConfig.valueType = "DEFAULT";
                                    repositoryConfig.resolve(effectiveConfig);
                                })
                                .fail(function (errorResponse) {
                                    repositoryConfig.reject(errorResponse);
                                })
                                .done();
                        } else {
                            repositoryConfig.reject(errorResponse);
                        }
                    })
                    .done();
            } else {
                repositoryConfig.reject(errorResponse);
            }
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Retrieve effective repository specific configs.
 * Returns the effective repository specific config for the tenant and repository ID passed. 
 * That can be either a custom value specified for the tenant/repository, the one of the tenant only or the default.
 *
 * @param {String} tenantId String The ID tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getEffectiveRepositoryConfigs(tenantId, repositoryId) {

    var repositoryConfig = Q.defer();
    var repositoryConfigCallComplete = Q.defer();
    logger.debug("Retrieving effective repository config for tenant " + tenantId + " and repository " + repositoryId);
    var configurations = [];
    repositoryConfigStoreModel.getRepositoryConfigs(tenantId, repositoryId)
        .then(function (repositoryConfigurations) {
            for (var repositoryConfigurationIndex = 0; repositoryConfigurationIndex < repositoryConfigurations.length;
                repositoryConfigurationIndex++) {
                var repositoryConfigEntry = repositoryConfigurations[repositoryConfigurationIndex];
                var builtrepositoryConfig = {
                    key: repositoryConfigEntry.key,
                    value: repositoryConfigEntry.value,
                    valueType: "CUSTOM"
                };
                configurations.push(builtrepositoryConfig);
            }
            repositoryConfigCallComplete.resolve();
        })
        .fail(function (errorResponse) {
            logger.debug("Ending processing of request as there has been a problem retrieving the information requested from the"
                + " store");
            repositoryConfigCallComplete.reject();
            repositoryConfig.reject(errorResponse);
            return repositoryConfig.promise;
        })
        .done();

    /** 
     * Using a second promise that doesn't leave this method to ensure that the repository specific config call considers also the tenant 
     * configs before calling the for global configs.
     */
    repositoryConfigCallComplete.promise
        .then(function () {
            return tenantConfigStoreModel.getTenantConfigs(tenantId);
        })
        .then(function (tenantConfigs) {
            for (var tenantConfigIndex = 0; tenantConfigIndex < tenantConfigs.length; tenantConfigIndex++) {
                var tenantConfigEntry = tenantConfigs[tenantConfigIndex];
                if (containsKey(configurations, tenantConfigEntry.key) === false) {
                    var builtEffectiveConfig = {
                        key: tenantConfigEntry.key,
                        value: tenantConfigEntry.default,
                        valueType: "DEFAULT"
                    };
                    configurations.push(builtEffectiveConfig);
                }
            }
            repositoryConfig.resolve(configurations);
        })
        .fail(function (errorResponse) {
            /** Taking no action if errorResponse is undefined as that will happen in a case where an error occured during the call for
             tenant specific configs.*/
            if (errorResponse !== undefined) {
                logger.debug("Unable to retrieve effective repository config for tenant " + tenantId + " repository " + repositoryId);
                repositoryConfigCallComplete.reject();
                repositoryConfig.reject(errorResponse);
                return repositoryConfig.promise;
            }
        })
        .done();

    /** 
     * Using a third promise that calls the globalconfigs.
     */
    repositoryConfigCallComplete.promise
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
            repositoryConfig.resolve(configurations);
        })
        .fail(function (errorResponse) {
            /** Taking no action if errorResponse is undefined as that will happen in a case where an error occured during the call for
             tenant specific configs.*/
            if (errorResponse !== undefined) {
                logger.debug("Unable to retrieve effective tenant config for tenant " + tenantId + " and repository " + repositoryId);
                repositoryConfig.reject(errorResponse);
            }
        })
        .done();

    return repositoryConfig.promise;
}

/**
 * Retrieve repository specific config.
 * Returns the repository specific config for this key that is stored against this tenant and repository.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @param {String} key String The custom configuration key.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getRepositoryConfig(tenantId, repositoryId, key) {
    var repositoryConfig = Q.defer();
    logger.debug("Retrieving repository specific config for tenant " + tenantId + ", repository " + repositoryId + "with key " + key);
    repositoryConfigStoreModel.getRepositoryConfig(tenantId, repositoryId, key)
        .then(function (repositoryConfigResult) {
            repositoryConfig.resolve(repositoryConfigResult);
        })
        .fail(function (errorResponse) {
            logger.debug("Unable to retrieve repository specific config for tenant " + tenantId + ", repository " + repositoryId
                + " with key " + key);
            repositoryConfig.reject(errorResponse);
        })
        .done();
    return repositoryConfig.promise;
}

/**
 * Retrieve repository specific configs as map of key-value pairs.
 * Returns all repository specific configs as a list.
 *
 * @param {String} tenantId String The ID of the tenant.
 * @param {String} repositoryId String The ID of the repository.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 **/
function getRepositoryConfigs(tenantId, repositoryId) {
    var repositoryConfig = Q.defer();
    var configurations = [];
    logger.debug("Retrieving repository specific config for tenant " + tenantId + " and repository " + repositoryId);
    repositoryConfigStoreModel.getRepositoryConfigs(tenantId, repositoryId)
        .then(function (repositoryConfigResult) {
            for (var i = 0; i < repositoryConfigResult.length; i++) {
                var repositoryConfigEntry = repositoryConfigResult[i];
                var config = {
                    key: repositoryConfigEntry.key,
                    value: repositoryConfigEntry.value
                };
                configurations.push(config);
            }
            repositoryConfig.resolve(configurations);
        })
        .fail(function (errorResponse) {
            logger.debug("Unable to retrieve tenant specific config for tenant " + tenantId + " and repository " + repositoryId);
            repositoryConfig.reject(errorResponse);
        })
        .done();
    return repositoryConfig.promise;
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
