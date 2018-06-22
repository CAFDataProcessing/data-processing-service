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
//config for the data-processing-service itself
var logger = require('./loggingHelper.js');

//environment variables that config will be pulled from
var dataProcessingServicePort = "CAF_PROCESSING_SERVICE_PORT";
var dataProcessingServiceAdminPort = "CAF_PROCESSING_SERVICE_ADMIN_PORT";
var dataProcessingServiceCacheDuration = "CAF_PROCESSING_SERVICE_CACHE_DURATION";

var dataProcessingServiceConfig = {
  cacheDuration: 600,
  port: 8080,
  adminPort: 8081
};

//Get API Port
var portEnv = process.env[dataProcessingServicePort];
if(portEnv!==null && portEnv!==undefined){
  dataProcessingServiceConfig.port = portEnv;
}

var portAdminEnv = process.env[dataProcessingServiceAdminPort];
if(portAdminEnv!==null && portAdminEnv!==undefined){
  dataProcessingServiceConfig.adminPort = portAdminEnv;
}

var cacheDurationEnv = process.env[dataProcessingServiceCacheDuration];
if(cacheDurationEnv!==null && cacheDurationEnv!==undefined){
  dataProcessingServiceConfig.cacheDuration = cacheDurationEnv;
}

module.exports = dataProcessingServiceConfig;
logger.debug(function(){return "Service config is: "+JSON.stringify(dataProcessingServiceConfig);});