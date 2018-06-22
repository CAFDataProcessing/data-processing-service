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
//TODO investigate logging library for NodeJS, likely no need to invent a new logging implementation
var loggingConfigHelper = require('./loggingConfigHelper.js');

module.exports = {
  debug: logDebug,
  error: logError,
  info: logInfo,
  warn: logWarn,
  warning: logWarning
};

//defining log level hierarchy.
var logLevels = {
  "DEBUG": 1,
  "INFO": 2,
  "WARNING": 3,
  "ERROR": 4
};

//defines minimum level to log. 
var logLevelSet = loggingConfigHelper.getLogLevel();
//if environment doesn't specify a valid log level default to 'INFO'
if(logLevelSet===null || logLevelSet===undefined || logLevels[logLevelSet]===undefined){
  logLevelSet = "INFO";
}
//record this to avoid looking it up every time in 'logToConsole'
var logLevelIndex = logLevels[logLevelSet];

var logToConsole = function(logLevel, logMessage){
  //only log at the level set and above
  if(logLevels[logLevel]<logLevelIndex){
    return;
  }
  //support passsing a function to generate message. Avoids running logic like 'JSON.stringify(object)' if a message wouldn't be logged due to level settings.
  var logOutputMessage = new Date().toUTCString()+ " "+logLevel+" "
  if(typeof(logMessage)==='function'){
    logOutputMessage += logMessage();
  }
  else{
    logOutputMessage += logMessage;
  }
  console.log(logOutputMessage.replace(new RegExp("\\n", 'g'), ""));
};
function logDebug(message){
  logToConsole("DEBUG", message);
}
function logInfo(message){
  logToConsole("INFO", message);
}
function logError(message){
  logToConsole("ERROR", message);
}
function logWarn(message){
  logToConsole("WARNING", message);
}
function logWarning(message){
  logWarn(message);
}

