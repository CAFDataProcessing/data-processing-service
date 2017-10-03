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
var logger = require('../helpers/loggingHelper.js');
var adminLib = require('../libs/admin.js');
var errorResponseHelper = require('../models/errorResponse.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');

module.exports = {
  getVersion: getVersion,
  healthCheck: healthCheck
};

//TODO expose on contract
function getVersion(req, res, next){
  var getVersionPromise = adminLib.getVersion();
  httpHelper.writePromiseJSONResultToResponse(getVersionPromise, res);
}

function healthCheck(req, res, next){
  var healthPromise = adminLib.healthCheck();
  httpHelper.writePromiseJSONResultToResponse(healthPromise, res);
}

//TODO expose on contract
function getLogLevel(req, res, next){
  var logLevelPromise = adminLib.getLogLevel();
  httpHelper.writePromiseJSONResultToResponse(logLevelPromise, res);
}