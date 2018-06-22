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
// handles communication requests with global config database table
const Sequelize = require('sequelize');
const Q = require('q');
const apiErrorFactory = require('../errors/apiErrorFactory.js');
const databaseDefinition = require('./databaseConnection.js').definition;
const globalConfigTableDetails = require('./tables/globalConfig.js');
const logger = require('../../helpers/loggingHelper.js');

module.exports = {
    deleteGlobalConfig: deleteGlobalConfig,
    getGlobalConfig: getGlobalConfig,
    getGlobalConfigs: getGlobalConfigs,
    setGlobalConfig: setGlobalConfig
}

const tableDefinition = globalConfigTableDetails.definition;
const globalConfigTable = databaseDefinition.define(globalConfigTableDetails.name,
    tableDefinition,
    { tableName:  globalConfigTableDetails.tableName, timestamps: false });

/**
 * Deletes a global config using specified parameter.
 * @param key {String} The key property identifying the global config that should be deleted.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of delete.
 *  Resolved promise will return an empty object.
 *  Rejected promise will pass an ApiError with either type set to ITEM_NOT_FOUND if no global config with the specified key
 *  could be found or type DATABASE_UNKNOWN_ERROR for any other kind of failure.
 */
function deleteGlobalConfig(key) {
    var deferredDelete = Q.defer();
    globalConfigTable.destroy({ where: { key: key }})
        .then(function(deleteResult){
            if(deleteResult < 1) {
                logger.error("No global config found for key '"+ key + "'.");
                deferredDelete.reject(apiErrorFactory.createNotFoundError(
                    "Global config specified for delete using key '"+key+"' was not found."));
                return;
            }
            deferredDelete.resolve({});
        })
        .catch(function(errorResponse){
            if(errorResponse instanceof Sequelize.ForeignKeyConstraintError) {
                logger.error("Cannot delete key '"+key+"' as it is in use by one or more tenant configs: "
                    + errorResponse.toString());
                deferredDelete.reject(apiErrorFactory.createDatabaseUnknownError('Failure during deletion of global config.' +
                    " One or more tenant config relies on the key '"+ key +"'."));
                return;
            }
            logger.error("Failure occurred during delete of key '"+ key + "': "+errorResponse.toString());
            deferredDelete.reject(apiErrorFactory.createDatabaseUnknownError("Failure during delete of global config with" +
                " key '" +key+ "'."));
        });
    return deferredDelete.promise;
}

/**
 * Retrieves the default and description for a global config using specified key.
 * @param key {String} the key property identifying the global config to use.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 *  Resolved promise will return an object containing the default and description for the matching global config.
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR or ITEM_NOT_FOUND if unable to find
 *  the specified global config.
 */
function getGlobalConfig(key) {
    var deferredGet = Q.defer();
    globalConfigTable.findOne({
            attributes: [tableDefinition.default.field, tableDefinition.description.field],
            where: { key: key}
        })
        .then(function(findResult) {
            if(findResult===null) {
                logger.error("Failed to find global config with key '"+ key + "'.");
                deferredGet.reject(apiErrorFactory.createNotFoundError("Failed to find global config with key '"+ key + "'."));
                return;
            }
            // the object returned can have additional properties specific to the database model, only return properties that
            // caller is interested in rather than letting these DB implementation properties leak out of this layer
            var builtGlobalConfig = {
                default: findResult.default,
                description: findResult.description,
                scope: "TENANT"
            };
            deferredGet.resolve(builtGlobalConfig);
        })
        .catch(function(errorResponse) {
            logger.error("Failure occurred trying to get global config for key '"+key+"': " + errorResponse.toString());
            deferredGet.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure during retrieval of global config with key '" + key+ "'."));
        })
        .done();

    return deferredGet.promise;
}

/**
 * Retrieves existing global configs. The first 100 global configs will be returned.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 * Resolved promise will return an array of global configs that match the schema defined in ./tables/globalConfig.js.
 * Rejected promise will pass an ApiError with type set to DATABASE_UNKNOWN_ERROR.
 */
function getGlobalConfigs() {
    var deferredGetAll = Q.defer();

    // defaulting number returned to the first 100. We may expand this in future to support passed in offsets and limits.
    globalConfigTable.findAll({ limit: 100 })
        .then(function(retrievedGlobalConfigs){
            // the objects returned have additional properties specific to the database model, only return properties that caller
            // is interested in
            var globalConfigsToReturn = [];
            for(var globalConfigIndex = 0; globalConfigIndex < retrievedGlobalConfigs.length; globalConfigIndex++ ) {
                var retrievedGlobalConfig = retrievedGlobalConfigs[globalConfigIndex];
                var builtGlobalConfig = {
                    key: retrievedGlobalConfig.key,
                    default: retrievedGlobalConfig.default,
                    description: retrievedGlobalConfig.description,
                    scope: "TENANT"
                };
                globalConfigsToReturn.push(builtGlobalConfig);
            }
            deferredGetAll.resolve(globalConfigsToReturn);
        })
        .catch(function(errorResponse) {
            logger.error("Failure occurred trying to get global configs: "+errorResponse.toString());
            deferredGetAll.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure occurred trying to get global configs."));
        })
        .done();

    return deferredGetAll.promise;
}

/**
 * Creates or updates a global config using the provided parameters.
 * @param key {String} the key to set for the global config. If this key is not already present for any existing global config
 * then it will be added as a new entry. Otherwise the global config with this key value will be updated.
 * @param defaultValue {String} the default to set on the global config.
 * @param description {String} the description to set on the global config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of create/update.
 *  Resolved promise will pass an empty object. Rejected promise will pass an ApiError with type set to DATABASE_UNKNOWN_ERROR.
 */
function setGlobalConfig(key, defaultValue, description) {
    var deferredSet = Q.defer();
    var setParam = {
        key: key,
        default: defaultValue,
        description: description
    };
    globalConfigTable.upsert(setParam)
        .then(function(upsertResult) {
            if(upsertResult) {
                logger.debug("Created global config in database successfully for key '" + key + "'.");
            }
            else {
                logger.debug("Updated global config in database successfully for key '" + key+"'.");
            }
            deferredSet.resolve({});
        })
        .catch(function(errorResponse) {
            logger.error("Failure occurred trying to create global config for key '"+key+"': " + errorResponse.toString());
            deferredSet.reject(apiErrorFactory.createDatabaseUnknownError('Failure during creation of global config.'));
        })
        .done();
    return deferredSet.promise;
}
