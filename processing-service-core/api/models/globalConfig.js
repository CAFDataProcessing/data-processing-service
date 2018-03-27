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
var Q = require('q');
var store = require("./db/globalConfigStore.js");
var logger = require('../helpers/loggingHelper.js');

module.exports = {
  setGlobalConfig: setGlobalConfig,
  deleteGlobalConfig: deleteGlobalConfig,
  getGlobalConfig: getGlobalConfig,
  getGlobalConfigs: getGlobalConfigs
};

/**
 * Creates or updates global config based on the globalConfigParams.
 * 
 * @param {String} globalConfigParams The json string represents key-value pair map of global configuration.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of create/update.
 */
function setGlobalConfig(globalConfigParams){
  return executeConfig(
    store.setGlobalConfig(
      globalConfigParams.key,
      globalConfigParams.default,
      globalConfigParams.description
    ),
    'Created global configuration',
    'Failed to create global configuration'
  );
};

/**
 * Retrieves global config related to the key.
 * 
 * @param {String} key The key of global config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 */
function getGlobalConfig(key){  
  return executeConfig(
    store.getGlobalConfig(key),
    'Retrieved global configuration',
    'Failed to retrieve global configuration'
  );
};

/**
 * Retrieves all global configs.
 * 
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of retrieval.
 */
function getGlobalConfigs(){
  return executeConfig(
    store.getGlobalConfigs(),
    'Retrieved all global configurations',
    'Failed to retrieve all global configurations'
  );
};

/**
 * Deletes global config related to the key.
 * 
 * @param {String} key The key of global config.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of deletion.
 */
function deleteGlobalConfig(key){
  return executeConfig(
    store.deleteGlobalConfig(key),
    'Deleted global configuration',
    'Failed to delete global configuration'
  );
};

/**
 * 
 * @param {String} successMsg The logger message in case of successful execution of called function.
 * @param {String} failMsg The logger message in case when called function fails.
 * @param {Promise} configPromise The promise returned by function.
 * @returns {*|d.promise|Function|promise|a|h} a promise that will be resolved or rejected based on result of called function.
 */
function executeConfig(configPromise, successMsg, failMsg){  
  var deferred = Q.defer();
  
  configPromise
  .then(function(result){ 
    logger.info(successMsg);
    deferred.resolve(result);
  })
  .fail(function(errorResponse){
    logger.info(failMsg);
    deferred.reject(errorResponse);
  });
  
  return deferred.promise;
};
