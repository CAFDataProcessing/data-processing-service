/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
'use strict';
var SwaggerExpress = require('swagger-express-mw');
var app = require('express')();
var logger = require('./api/helpers/loggingHelper.js');
var appConfig = require('./api/helpers/dataProcessingServiceConfigHelper.js');
var swaggerHelper = require('./api/helpers/swaggerHelper.js');
var requestProcessing = require('./api/libs/requestProcessing.js');

module.exports = app; // for testing

var config = {
  appRoot: __dirname, // required config
  serverPort: appConfig.port
};

//TODO move this out of main app to a utility based area
var logErrorToConsole = function(err, statusCode, statusMessage){
  var errorOutputString = "Error response returned: ";
  //output status code and status message
  if(statusCode!=undefined){
    errorOutputString += statusCode + " ";
  }
  if(statusMessage!=undefined){
    errorOutputString += statusMessage + " ";
  }
  
  //append the error in full form
  errorOutputString += JSON.stringify(err);
  //output original response if there
  if(err.originalResponse){
    errorOutputString += "\r\noriginalResponse was: " +err.originalResponse;
  }
  //log full error
  logger.error(errorOutputString);
};

SwaggerExpress.create(config, function(err, swaggerExpress) { 
  if (err) { throw err; }
  
  //log all non 2xx JSON responses, need access to response message so overriding the res.json method with an implementation to call the logger
  //then proceed as normal
  app.use(function(req, res, next){
    var origJson = res.json;
    res.json = function(jsonObject){
      res.json = origJson;
      if(jsonObject && (res.statusCode < 200 || res.statusCode > 299)){
        logErrorToConsole(jsonObject, res.statusCode, res.statusMessage);
      }
      res.json(jsonObject);
    }
    next();
  });
  
  //plug in point for any additional pre-processing required
  app.use(function(req, res, next){
    requestProcessing.preProcess(req, res);    
    next();
  });
  
  // install middleware
  swaggerExpress.register(app);
  //set up additional swagger paths not in contract
  swaggerHelper.register(config, app, swaggerExpress);
  
  //plug in point for any additional post-processing required
  app.use(function(req, res, next){
    requestProcessing.postProcess(req, res);      
    next();
  });
  
  // Custom error handler that returns JSON in line with validation requirements
  app.use(function(err, req, res, next) {
    var outputErrorMessage;
    
    if (typeof err !== 'object') {
      // If the object is not an Error, create a representation that appears to be
      err = {
        message: String(err) // Coerce to string
      };
      outputErrorMessage = err.message;
    } else {
      // Ensure that err.message is enumerable (It is not by default)
      Object.defineProperty(err, 'message', { enumerable: true });
      
      //if this was a swagger schema validation issue output all messages in results
      outputErrorMessage = err.message;
      if(err.code === 'SCHEMA_VALIDATION_FAILED' && err.results !== undefined && err.results.errors!==undefined){
        for(var validationError of err.results.errors){
          outputErrorMessage += ". "+ validationError.message;
        }
      }
    }     
    logErrorToConsole(err);
    // Return a JSON representation of #/definitions/ErrorResponse
    res.setHeader('Content-Type', 'application/json');
    res.json({message: outputErrorMessage});
  });
  
  app.listen(config.serverPort);
  logger.info('Service listening on port: '+config.serverPort);
});
