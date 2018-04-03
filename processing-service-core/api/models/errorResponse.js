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
var ApiError = require('./errors/apiError.js');
var apiErrorTypes = require('./errors/apiErrorTypes.js');

//object used to hold information returned in event of error when using processing API. The response body and status code can be stored on this for later use. Can convert internal api error types to HTTP response representations

module.exports = {
  create: create,
  writeErrorToResponseJSON: writeErrorToResponseJSON
};

//returns a HTTP Status Code representing the ApiErrorType
function getStatusCodeFromApiErrorType(apiErrorType){
  var statusCode;
  switch(apiErrorType){
    case apiErrorTypes.UNKNOWN:
      statusCode = 500;
      break;
    case apiErrorTypes.ITEM_NOT_FOUND:
      statusCode = 404;
      break;
    case apiErrorTypes.METHOD_NOT_ALLOWED:
      statusCode = 405;
      break;
    default:
      statusCode = 500;
  }
  return statusCode;
}

function create(errorResponse, statusCode){
  var returnObject = {
    statusCode: statusCode || 500
  };
  var responseToSet = null;
  var errorResponseType= typeof(errorResponse);
  
  if(errorResponse instanceof Error){
    responseToSet = {
        message: errorResponse.toString()
      };
  }
  else if(errorResponse instanceof ApiError){
    responseToSet = {
      message: errorResponse.message,      
    };
    returnObject.statusCode = getStatusCodeFromApiErrorType(errorResponse.type);
  }
  else if(errorResponseType!=='object'){
      responseToSet = {
        message: errorResponse
      };
  }
  else{
    responseToSet = errorResponse;
  }
  returnObject.response = responseToSet;

  return returnObject;
}

function writeErrorToResponseJSON(error, responseToWriteTo){
  var errorResponse = create(error);
  responseToWriteTo.status(errorResponse.statusCode);
  responseToWriteTo.json(errorResponse.response);
}

function writeErrorToLog(error){
  var errorResponse = create(error);
  logger.error(error.status + " " + error.message);
}