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
//administrative functions that give detail about the data processing service.
var Q = require('q');
var util = require('util');
var packageJson = require('../../package.json');
var policyHttpHelper = require('../helpers/policyHttpHelper.js');
var loggingConfigHelper = require('../helpers/loggingConfigHelper.js');

const unHealthy = 'UNHEALTHY';
const healthy = 'HEALTHY';

//Queries the health of the service. Check any external services relied on also. Returns a promise.
module.exports.healthCheck = function(){
  var deferredRequest = Q.defer();
  var dependantStatuses = [];
  
  //get status of Policy API
  var policyHealthPromise = policyHttpHelper.healthCheck();
  var policyApiName = 'POLICY_API';
  var serviceStatus = healthy;
  policyHealthPromise.then(function(result){
    if(result !== true){
      throw "Health Check did not return 'true'. Returned " + result;
    }
    dependantStatuses.push({
      name: policyApiName,
      status: healthy
    });
    return dependantStatuses;
  })
  .fail(function(errorResponse){  
    dependantStatuses.push({
      name: policyApiName,
      message: typeof(errorResponse) === 'string' ? errorResponse : util.inspect(errorResponse),
      status: unHealthy
    });
    serviceStatus = unHealthy;
    return dependantStatuses;
  })
  .then(function(result){
    //check if dependencies were success or failure
    if(serviceStatus===unHealthy){
      deferredRequest.reject({
        status: unHealthy,
        dependencies: dependantStatuses
      });
    }
    else{
      deferredRequest.resolve({
        status: healthy,
        dependencies: dependantStatuses
      });
    }    
  }).done();
    
  return deferredRequest.promise;
};

//Retrieves the version of this data-processing-service. In future may return the version of any external services used that provide that information.
module.exports.getVersion = function() {
  return Q({
    api: packageJson.version
    });
};

module.exports.getLogLevel = function(){
  return Q({
    level: loggingConfigHelper.getLogLevel()
  });
};

module.exports.setLogLevel = function(newLogLevel){
  return Q({
    level: loggingConfigHelper.setLogLevel(newLogLevel)
  });
};