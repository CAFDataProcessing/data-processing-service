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
//helper providing simple callbacks to handle promise resolution from calls to external APIs, such as Policy API.
var errorResponseHelper = require('../models/errorResponse.js');

module.exports = {
  handleDeleteResponse: handleDeleteResponse,
  handleDeleteResponseAndThrow: handleDeleteResponseAndThrow,
  handleFailure: handleFailure,
  handlePotentialSuccess: handlePotentialSuccess,
  writeCreatePromiseJSONResultToResponse: writeCreatePromiseJSONResultToResponse,
  writeDeletePromiseJSONResultToResponse: writeDeletePromiseJSONResultToResponse,
  writePromiseJSONResultToResponse: writePromiseJSONResultToResponse,
  writeUpdatePromiseJSONResultToResponse: writeUpdatePromiseJSONResultToResponse
};
//generic success callback for deferred promises that return HTTP responses.
//deferrred - the deferred object to call rseolve or reject on based on resultObject
//extractFunction - Optional. A function to pass the JSON parsed from the result body to for further extraction before resolve is called
function handlePotentialSuccess(deferred, extractFunction){
  return function(response, responseBody){
    var resultObject = JSON.parse(responseBody);
    if(response.statusCode >= 200 && response.statusCode <=299){
      if(typeof(extractFunction)==='function'){
        deferred.resolve(extractFunction(resultObject));
      }
      else{
        deferred.resolve(resultObject);
      }
    }
    else {
      deferred.reject(errorResponseHelper.create(resultObject, response.statusCode));
    }
  };
}
//generic failure callback that handles deferred TODO move to common place for all scripts to use
function handleFailure(deferred){
  return function(errorResponse){
    deferred.reject(errorResponseHelper.create(errorResponse, errorResponse.statusCode));
  };
}

//a delete response may be returned as a status 200 with a JSON body detailing an error. Returns the error message if that happened on the response passed in, otherwise returns null.
function handleDeleteResponse(resultOfDelete){
  //delete returns an object and in event it cannot find the item to delete sets an error message and returns status 200.
  if(resultOfDelete!==null && resultOfDelete!==undefined &&
    resultOfDelete.result !==null && resultOfDelete.result !==undefined && resultOfDelete.result.length > 0 ){
    if(resultOfDelete.result[0].error_message!==null){
      return resultOfDelete.result[0].error_message;
    }      
  }
  return null;
}

//a delete response may be returned as a status 200 with a JSON body detailing an error. Will throw with the message returned if an error occurred.
function handleDeleteResponseAndThrow(resultOfDelete){
  var response = handleDeleteResponse(resultOfDelete);
  if(response!==null){
    throw response;
  }
}

function writePromiseJSONResultToResponse(promise, response, statusCode){
  promise.then(function(result){
    if(statusCode!==undefined){
      response.status(statusCode);
    }
    response.json(result);
  })
  .fail(function(errorResponse){
    errorResponseHelper.writeErrorToResponseJSON(errorResponse, response);
  }).done();
}

function writeCreatePromiseJSONResultToResponse(promise, response){
  writePromiseJSONResultToResponse(promise, response, 201);
}

function writeDeletePromiseJSONResultToResponse(promise, response){
  writePromiseJSONResultToResponse(promise, response, 204);
}

function writeUpdatePromiseJSONResultToResponse(promise, response){
  writePromiseJSONResultToResponse(promise, response, 204);
}