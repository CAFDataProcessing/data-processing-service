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
var logger = require('./loggingHelper.js');

//environment variables that config will be pulled from
var policyAPIHost = "CAF_PROCESSING_SERVICE_POLICY_API_HOST";
var policyAPIPort = "CAF_PROCESSING_SERVICE_POLICY_API_PORT";
var policyAPIEntryPath = "CAF_PROCESSING_SERVICE_POLICY_API_ENTRY_PATH";

var policyConfig = {
  policyAPIHost: "localhost",
  policyAPIPort: "8080",
  policyAPIEntryPath: "/corepolicy/"
};
//update config with policygateway environment options if any were passed
//Get API Host
var policyHostEnv = process.env[policyAPIHost];
if(policyHostEnv!==null && policyHostEnv!==undefined){
  policyConfig.policyAPIHost = policyHostEnv;
}
//GET API Port
var policyPortEnv = process.env[policyAPIPort];
if(policyPortEnv!==null && policyPortEnv!==undefined){
  policyConfig.policyAPIPort = policyPortEnv;
}
//GET API Entry Path (url path on machine that API is deployed at)
var policyEntryPathEnv = process.env[policyAPIEntryPath];
if(policyEntryPathEnv!==null && policyEntryPathEnv!==undefined){
  policyConfig.policyAPIEntryPath = policyEntryPathEnv;
}
exports.policyConfig = policyConfig;
logger.debug(function(){return "Policy API config is: "+JSON.stringify(policyConfig);});