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
// configuration for communication with any databases the processing service may contact
var logger = require('./loggingHelper.js');

/**
 * Sets the specified property on database configuration using either the value set for the named environment variable or the
 * default value provided.
 * @param propertyName configuration property to set
 * @param environmentVariableName name of the environment variable to use value from if set
 * @param defaultValue default value to set the property on config to if the environment variable is not set
 */
function setConfigProperty(propertyName, environmentVariableName, defaultValue) {
    var environmentVariableValue = process.env[environmentVariableName];
    if(environmentVariableValue!==null && environmentVariableValue!==undefined){
        databaseConfig[propertyName] = environmentVariableValue;
    }
    else {
        databaseConfig[propertyName] = defaultValue;
    }
}

//environment variables that config will be pulled from
var databaseHostEnvName = "CAF_PROCESSING_SERVICE_DATABASE_HOST";
var databaseNameEnvName = "CAF_PROCESSING_SERVICE_DATABASE_NAME";
var databasePasswordEnvName = "CAF_PROCESSING_SERVICE_DATABASE_PASSWORD";
var databasePortEnvName = "CAF_PROCESSING_SERVICE_DATABASE_PORT";
var databaseUserEnvName = "CAF_PROCESSING_SERVICE_DATABASE_USERNAME";

// defining the database configuration
var databaseConfig = {};
setConfigProperty('host', databaseHostEnvName, 'localhost');
setConfigProperty('name', databaseNameEnvName, 'workflow');
setConfigProperty('password', databasePasswordEnvName, '');
setConfigProperty('port', databasePortEnvName, 5432);
setConfigProperty('username', databaseUserEnvName, 'root');

module.exports = databaseConfig;
logger.debug(function(){return "Service config is: "+JSON.stringify(databaseConfig);});


