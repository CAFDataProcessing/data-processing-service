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
var apiErrorFactory = require('../models/errors/apiErrorFactory.js');

var Q = require('q');
var configurations = {};

module.exports.setGlobalConfig = function(key, defaultVal, description) {
  var deferredSetConfig = Q.defer();
    
  var config = {
    default: defaultVal,
    description: description
  };
  configurations[key] = config;
  deferredSetConfig.resolve({});
//  deferredSetConfig.reject(apiErrorFactory.createNotFoundError("Failure during creation of global config"));
  return deferredSetConfig.promise;
};

module.exports.getGlobalConfig = function(key) {
  var deferredGetConfig = Q.defer();
  
  var globalConfig = {};
  var config = configurations[key];
  if(config===undefined) {
    deferredGetConfig.reject(apiErrorFactory.createNotFoundError("Failed to find global config with key '" + key + "'."));
  } else {
      globalConfig["default"] = config.default;
      globalConfig["description"] = config.description;
      
      deferredGetConfig.resolve(globalConfig);
  }
  
  return deferredGetConfig.promise;
};

module.exports.getGlobalConfigs = function() {
  var deferredGetConfigs = Q.defer();
  var resultConfigurations = [];
  
  for (var key in configurations) {
    resultConfigurations.push(
      {
        "key": key,
        "default": configurations[key].default,
        "description": configurations[key].description
      }    
    ); 
  }
  
  deferredGetConfigs.resolve(resultConfigurations);
//  deferredGetConfigs.reject(apiErrorFactory.createNotFoundError("Failure occurred trying to get global configs"));
   
  return deferredGetConfigs.promise;
};

module.exports.deleteGlobalConfig = function(key) {
  var deferredDeleteConfig = Q.defer();
  
  var config = configurations[key];
  if(config===undefined) {
    deferredDeleteConfig.reject(
            apiErrorFactory.createNotFoundError("Global config specified for delete using key '" + key + "' was not found."));
  } else {
    delete configurations[key];
    deferredDeleteConfig.resolve({});
  }
  
  return deferredDeleteConfig.promise;
};
