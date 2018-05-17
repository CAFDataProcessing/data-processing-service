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
var policyConfig = require('../helpers/policyConfigHelper.js').policyConfig;
var packageJson = require('../../package.json');
var loggingConfigHelper = require('../helpers/loggingConfigHelper.js');
var request = require('request');
var logger = require('../helpers/loggingHelper.js');

const Sequelize = require('sequelize');
const databaseConfig = require('../helpers/processingDatabaseConfigHelper.js');

const unHealthy = 'UNHEALTHY';
const healthy = 'HEALTHY';

module.exports.healthCheck = function(onCompleteDeferred){

  var policyApiStatus = {
    name: 'POLICY_API',
    status: unHealthy
  }; 

  var databaseStatus = {
    name: 'PROCESSING_DATABASE',
    status: unHealthy
  };

  var healthCheckResult = {
    status: unHealthy,
    dependencies: [policyApiStatus, databaseStatus]
  }

  var policyHealthCheckDeferred = Q.defer();
  policyHealthCheckDeferred.promise.then(function(response){
    var healthStatusCode = (response.statusCode >= 200 && response.statusCode <=299);
    policyApiStatus.status =  healthStatusCode ? healthy : unHealthy;

    if(!healthStatusCode){
      policyApiStatus.message = response.statusMessage;
    }
  })
  .fail(function(error){
    policyApiStatus.message = error;
  });

  makePolicyHealthCheckRequest(policyHealthCheckDeferred);

  var databaseHealthCheckDeferred = Q.defer();
  databaseHealthCheckDeferred.promise.then(function(dbHealthResult){
    databaseStatus.status = dbHealthResult.status;
    databaseStatus.message = dbHealthResult.message;
  })
  .fail(function(error){
    databaseStatus.message = error;
  });

  makeDatabaseHealthCheckRequest(databaseHealthCheckDeferred);

  Q.all([policyHealthCheckDeferred.promise, databaseHealthCheckDeferred.promise]).then(function(){
    healthCheckResult.status = policyApiStatus.status===healthy && databaseStatus.status===healthy ? healthy : unHealthy;
    onCompleteDeferred.resolve(healthCheckResult);
  }).fail(function(error){
    onCompleteDeferred.reject(error);
  });
};

function makePolicyHealthCheckRequest(policyHealthCheckDeferred){
  var policyHealthCheckRequest = request.get({
    baseUrl: 'http://' + policyConfig.policyAPIHost + ':' + policyConfig.policyAPIPort + policyConfig.policyAPIEntryPath,
    uri: 'healthcheck',
    qs: {
        project_id: 'healthcheck'
    }
  }, function (error, response, body) {
    if(error){
      policyHealthCheckDeferred.reject(error);
    }
    else {
      policyHealthCheckDeferred.resolve(response);
    }
  });
}

function makeDatabaseHealthCheckRequest(databaseHealthCheckDeferred){

  var databaseDefinition = new Sequelize(databaseConfig.name, databaseConfig.username, databaseConfig.password,
    {
        dialect: 'postgres',
        host: databaseConfig.host,
        logging: logger.debug,
        operatorsAliases: false,
        port: databaseConfig.port
    }
  );

  databaseDefinition.authenticate()
  .then(function() {
    databaseHealthCheckDeferred.resolve({status: healthy});
  })
  .catch(function(err) {
      logger.error('Unable to connect to the database: ' + err);
      databaseHealthCheckDeferred.resolve({status: unHealthy, message: 'Unable to connect to the database'});
  });

}

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
