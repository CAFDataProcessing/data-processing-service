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
// handles communication requests with tenant config database table
const Sequelize = require('sequelize');
const Q = require('q');
const apiErrorFactory = require('../errors/apiErrorFactory.js');
const databaseDefinition = require('./databaseConnection.js').definition;
const logger = require('../../helpers/loggingHelper.js');
const tenantConfigTableDetails = require('./tables/tenantConfig.js');

module.exports = {
    deleteTenantConfig: deleteTenantConfig,
    getTenantConfig: getTenantConfig,
    getTenantConfigs: getTenantConfigs,
    setTenantConfig: setTenantConfig
}

const tableDefinition = tenantConfigTableDetails.definition;
const tenantConfigTable = databaseDefinition.define(tenantConfigTableDetails.name,
    tableDefinition,
    { tableName:  tenantConfigTableDetails.tableName, timestamps: false });

/**
 * Deletes a tenant config matching the specified parameters.
 * @param tenantId {String} the tenant ID identifying the config that should be deleted.
 * @param key {String} the key identifying the config that should be deleted.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of delete.
 *  Resolved promise will return an empty object.
 *  Rejected promise will pass an ApiError with either type set to ITEM_NOT_FOUND if no tenant config with the specified tenant ID
 *  and key could be found or type DATABASE_UNKNOWN_ERROR for any other kind of failure.
 */
function deleteTenantConfig(tenantId, key) {
    var deferredDelete = Q.defer();

    tenantConfigTable.destroy({ where: { tenantId: tenantId, key: key }})
        .then(function(deleteResult){
            if(deleteResult < 1) {
                logger.debug("No tenant config found for tenantId '"+tenantId+"' and key '"+ key +"' found.");
                deferredDelete.reject(apiErrorFactory.createNotFoundError(
                    "Tenant config specified for delete using tenantId '"+tenantId+"' and key '"+key+"' was not found."));
                return;
            }
            deferredDelete.resolve({});
        })
        .catch(function(errorResponse){
            logger.error("Failure occurred during delete of tenant config with tenantId '"
                +tenantId+"' and key '"+ key + "': "+errorResponse.toString());
            deferredDelete.reject(apiErrorFactory.createDatabaseUnknownError("Failure during delete of tenant config with" +
                " tenantId '" +tenantId+ "' and key '" +key+ "'."));
        });

    return deferredDelete.promise;
}

/**
 * Retrieves the value for a tenant config matching the specified parameters.
 * @param tenantId {String} the tenant ID identifying the tenant config.
 * @param key {String} the key identifying the tenant config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 *  Resolved promise will return the value on the matched tenant config.
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR or ITEM_NOT_FOUND if unable to find
 *  the specified tenant config.
 */
function getTenantConfig(tenantId, key) {
    var deferredGet = Q.defer();

    tenantConfigTable.findOne({
            attributes: [tableDefinition.value.field],
            where: { tenantId: tenantId, key: key}
        })
        .then(function(findResult) {
            if(findResult===null) {
                logger.error("Failed to find tenant config with tenantId '"+tenantId+"' and key '"+key+"'.");
                deferredGet.reject(apiErrorFactory.createNotFoundError(
                    "Failed to find tenant config with tenantId '"+tenantId+"' and key '"+key+"'."));
                return;
            }
            deferredGet.resolve(findResult.value);
        })
        .catch(function(errorResponse) {
            logger.error("Failure occurred trying to get tenant config with tenantId '"+tenantId+"' and key '"+key+
                "': " + errorResponse.toString());
            deferredGet.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure during retrieval of tenant config with tenantId '"+tenantId+"' and key '"+key+"'."));
        })
        .done();

    return deferredGet.promise;
}

/**
 * Retrieves existing tenant configs for the specified tenant. The first 100 tenant configs will be returned.
 * @param tenantId {String} the tenant ID to return tenant configs for.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of retrieval.
 *  Resolved promise will return an array of containing the keys and values for the tenant configs matched by the tenant ID.
 *  e.g. [ { 'key': entity', 'value': 'ip_address' } ]
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR.
 */
function getTenantConfigs(tenantId) {
    var deferredGetAll = Q.defer();

    // defaulting number returned to the first 100. We may expand this in future to support passed in offsets and limits.
    tenantConfigTable.findAll({
            attributes: [tableDefinition.key.field, tableDefinition.value.field],
            limit: 100,
            where: { tenantId: tenantId }
        })
        .then(function(retrievedTenantConfigs){
            // the objects returned have additional properties specific to the database model, only return properties that caller
            // is interested in
            var tenantConfigsToReturn = [];
            for(var configIndex=0; configIndex < retrievedTenantConfigs.length; configIndex++) {
                var retrievedTenantConfig = retrievedTenantConfigs[configIndex];
                var builtTenantConfig = {
                    key: retrievedTenantConfig.key,
                    value: retrievedTenantConfig.value
                };
                tenantConfigsToReturn.push(builtTenantConfig);
            }

            deferredGetAll.resolve(tenantConfigsToReturn);
        })
        .catch(function(errorResponse) {
            logger.error("Failure occurred trying to get tenant configs with tenantId '"+tenantId+"': "+errorResponse.toString());
            deferredGetAll.reject(apiErrorFactory.createDatabaseUnknownError(
                "Failure occurred trying to get tenant configs with tenantId '"+tenantId+"'."));
        })
        .done();

    return deferredGetAll.promise;
}

/**
 * Creates or updates a tenant config using the provided parameters. If the combination of tenant ID and key passed matches an
 * existing tenant config then the existing tenant config will be updated with the passed value otherwise a new tenant config
 * will be created. The key must match an existing global config key or this operation will fail.
 * @param tenantId {String} the tenant ID for the tenant config.
 * @param key {String} the key to set for the tenant config. The key must match an existing global config key or this
 * operation will fail.
 * @param value {String} the value to set for the tenant config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on the result of create/update.
 *  Resolved promise will pass an empty object.
 *  Rejected promise can pass an ApiError with type set to either DATABASE_UNKNOWN_ERROR or ITEM_NOT_FOUND if unable to find
 *  an existing global config with the specified key.
 */
function setTenantConfig(tenantId, key, value) {
    var deferredSet = Q.defer();
    var setParam = {
        tenantId: tenantId,
        key: key,
        value: value
    };
    tenantConfigTable.upsert(setParam)
        .then(function(upsertResult) {
            if(upsertResult) {
                logger.debug("Created tenant config in database successfully for tenantId '"+tenantId+"' and key '" + key + "'.");
            }
            else {
                logger.debug("Updated tenant config in database successfully for tenantId '"+tenantId+"' and key '" + key + "'.");
            }
            deferredSet.resolve({});
        })
        .catch(function(errorResponse) {
            if(errorResponse instanceof Sequelize.ForeignKeyConstraintError) {
                logger.error("No global config exists for key '"+key+"' so cannot create/update tenant config: "
                    + errorResponse.toString());
                deferredSet.reject(apiErrorFactory.createNotFoundError('Failure during creation of tenant config.' +
                    " No global config exists to override for key '"+ key +"'."));
                return;
            }

            logger.error("Failure occurred trying to create tenant config for key '"+key+"': " + errorResponse.toString());
            deferredSet.reject(apiErrorFactory.createDatabaseUnknownError('Failure during creation of tenant config.'));
        })
        .done();
    return deferredSet.promise;
}
