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
// handles communication requests with repository config database table
const Sequelize = require('sequelize');
const Q = require('q');
const apiErrorFactory = require('../errors/apiErrorFactory.js');
const databaseDefinition = require('./databaseConnection.js').definition;
const logger = require('../../helpers/loggingHelper.js');
const repositoryConfigTableDetails = require('./tables/repositoryConfig.js');
const globalConfigTableDetails = require('./tables/globalConfig.js');

module.exports = {
    deleteRepositoryConfig: deleteRepositoryConfig,
    getRepositoryConfig: getRepositoryConfig,
    getRepositoryConfigs: getRepositoryConfigs,
    setRepositoryConfig: setRepositoryConfig
}

const tableDefinition = repositoryConfigTableDetails.definition;
const repositoryConfigTable = databaseDefinition.define(repositoryConfigTableDetails.name,
    tableDefinition,
    { tableName: repositoryConfigTableDetails.tableName, timestamps: false });
const tableDefinition2 = globalConfigTableDetails.definition;
const globalConfigTable = databaseDefinition.define(globalConfigTableDetails.name,
    tableDefinition2,
    { tableName: globalConfigTableDetails.tableName, timestamps: false });

/**
 * Deletes a repository config matching the specified parameters.
 * @param repositoryId {String} the repository ID identifying the config that should be deleted.
 * @param tenanatId {String} the tenant ID identifying the config that should be deleted.
 * @param key {String} the key identifying the config that should be deleted.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of delete.
 *  Resolved promise will return an empty object.
 *  Rejected promise will pass an ApiError with either type set to ITEM_NOT_FOUND if no repository config with the specified repository ID
 *  and key could be found or type DATABASE_UNKNOWN_ERROR for any other kind of failure.
 */
function deleteRepositoryConfig(tenantId, repositoryId, key) {
    var deferredDelete = Q.defer();

    repositoryConfigTable.destroy({ where: { repositoryId: repositoryId, key: key, tenantId: tenantId } })
        .then(function (deleteResult) {
            if (deleteResult < 1) {
                logger.debug("No repository config found for repositoryId '" + repositoryId + "', key '" + key + "', and tenantId '"
                    + tenantId + "' found.");
                deferredDelete.reject(apiErrorFactory.createNotFoundError(
                    "Repository config specified for delete using repositoryId '" + repositoryId + "', key '" + key + "', and tenantId '"
                    + tenantId + "' was not found."));
                return;
            }
            deferredDelete.resolve({});
        })
        .catch(function (errorResponse) {
            logger.error("Failure occurred during delete of repository config with repositoryId '"
                + repositoryId + "', key '" + key + "', and tenantId '" + tenantId + "': " + errorResponse.toString());
            deferredDelete.reject(apiErrorFactory.createDatabaseUnknownError("Failure during delete of repository config with" +
                " repositoryId '" + repositoryId + "', key '" + key + "', and tenantId '" + tenantId + "'."));
        });

    return deferredDelete.promise;
}

/**
 * Retrieves the value for a repository config matching the specified parameters.
 * @param repositoryId {String} the repository ID identifying the repository config.
 * @param tenanatId {String} the tenant ID identifying the config that should be retrieved.
 * @param key {String} the key identifying the repository config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 *  Resolved promise will return the value on the matched repository config.
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR or ITEM_NOT_FOUND if unable to find
 *  the specified repository config.
 */
function getRepositoryConfig(tenantId, repositoryId, key) {
    var deferredGet = Q.defer();

    repositoryConfigTable.findOne({
        attributes: [tableDefinition.value.field],
        where: { repositoryId: repositoryId, key: key, tenantId: tenantId }
    })
        .then(function (findResult) {
            if (findResult === null) {
                logger.error("Failed to find repository config with repositoryId '" + repositoryId + "', key '" + key 
                + "', and tenantId '" + tenantId + "'.");
                deferredGet.reject(apiErrorFactory.createNotFoundError(
                    "Failed to find repository config with repositoryId '" + repositoryId + "', key '" + key + "', and tenantId '"
                    + tenantId + "'."));
                return;
            }
            deferredGet.resolve(findResult.value);
        })
        .catch(function (errorResponse) {
            logger.error("Failure occurred trying to get repository config with repositoryId '" + repositoryId + "', key '" + key
                + "', and tenantId '" + tenantId + "': " + errorResponse.toString());
            deferredGet.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure during retrieval of tenant config with repositoryId '" + repositoryId + "', key '" + key + "', and tenantId '"
                + tenantId + "'."));
        })
        .done();

    return deferredGet.promise;
}

/**
 * Retrieves existing repository configs for the specified tenant and repository. The first 100 repository configs will be returned.
 * @param repositoryId {String} the repository ID to return repository configs for.
 * @param tenanatId {String} the tenant ID identifying the config that should be deleted.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 *  Resolved promise will return an array of containing the keys and values for the repository configs matched by the tenant ID 
 *  and repository ID.
 *  e.g. [ { 'key': entity', 'value': 'ip_address' } ]
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR.
 */
function getRepositoryConfigs(tenantId, repositoryId) {
    var deferredGetAll = Q.defer();

    // defaulting number returned to the first 100. We may expand this in future to support passed in offsets and limits.
    repositoryConfigTable.findAll({
        attributes: [tableDefinition.key.field, tableDefinition.value.field],
        limit: 100,
        where: { repositoryId: repositoryId, tenantId: tenantId }
    })
        .then(function (retrievedRepositoryConfigs) {
            // the objects returned have additional properties specific to the database model, only return properties that caller
            // is interested in
            var repositoryConfigsToReturn = [];
            for (var configIndex = 0; configIndex < retrievedRepositoryConfigs.length; configIndex++) {
                var retrievedRepositoryConfig = retrievedRepositoryConfigs[configIndex];
                var builtRepositoryConfig = {
                    key: retrievedRepositoryConfig.key,
                    value: retrievedRepositoryConfig.value
                };
                repositoryConfigsToReturn.push(builtRepositoryConfig);
            }

            deferredGetAll.resolve(repositoryConfigsToReturn);
        })
        .catch(function (errorResponse) {
            logger.error("Failure occurred trying to get repository configs with repositoryId '" + repositoryId + "' and tenantId '"
                + tenantId + "': " + errorResponse.toString());
            deferredGetAll.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure occurred trying to get repository configs with repositoryId '" + repositoryId + "' and tenantId '" 
                + tenantId + "'."));
        })
        .done();

    return deferredGetAll.promise;
}

/**
 * Creates or updates a repository config using the provided parameters. If the combination of repository ID, tenant ID and key 
 * passed matches an existing repository config then the existing repository config will be updated with the passed value otherwise 
 * a new repository config will be created. The key must match an existing global config key, and the same key should be associated with
 * a scope == 1 or this operation will fail.
 * @param tenantId {String} the tenant ID for the tenant config. The id must match an existing tenantId or this
 * operation will fail.
 * @param repositoryId {String} the repository ID for the repository config.
 * @param key {String} the key to set for the repository config. The key must match an existing global config key or this
 * operation will fail.
 * @param value {String} the value to set for the repository config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of create/update.
 *  Resolved promise will pass an empty object.
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR or ITEM_NOT_FOUND if unable to find
 *  an existing global config with the specified key.
 */
function setRepositoryConfig(tenantId, repositoryId, key, value) {
    var deferredSet = Q.defer();
    var validScope = 1;
    var setParam = {
        repositoryId: repositoryId,
        tenantId: tenantId,
        key: key,
        value: value
    };

    globalConfigTable.findOne({
        where: { key: key, scope: validScope }
    }).then(function (findResult) {
        if (findResult === null) {
            logger.info("Failed to find the key '" + key + "' with valid scope in the global config table.");
            deferredSet.reject(apiErrorFactory.createNotFoundError(
                "Failed to find the key '" + key + "' with valid scope in the global config table."));
            throw new Error("key not found or invalid scope!");
        }
    }).then(function (handleRepositoryTable) {
        return repositoryConfigTable.upsert(setParam)
            .then(function (upsertResult) {
                if (upsertResult) {
                    logger.info("Created repository config in database successfully for repositoryId '" + repositoryId + "', key '" + key
                        + "', and tenantId '" + tenantId + "'.");
                }
                else {
                    logger.info("Updated repository config in database successfully for repositoryId '" + repositoryId + "', key '" + key
                        + "', and tenantId '" + tenantId + "'.");
                }
                deferredSet.resolve({});
            })
    }).catch(function (errorResponse) {
        logger.error("Failure occurred trying to create repository config for repositoryId '" + repositoryId + "', key '" + key
            + "', and tenantId '" + tenantId + "'. The specific message is: '" + errorResponse.toString() + "'");
        deferredSet.reject(apiErrorFactory.createDatabaseUnknownError('Failure during creation of repository config.'));
    })
        .done();
    return deferredSet.promise;
}
