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
var Q = require('q');
var http = require('http');
var querystring = require('querystring');
var policyConfig = require('./policyConfigHelper.js').policyConfig;
var logger = require('./loggingHelper.js');
var promiseHelper = require('./httpPromiseHelper.js');

module.exports = {
  genericPolicyAPIGetItemRequest: genericPolicyAPIGetItemRequest,
  genericPolicyAPIGetItemsRequest: genericPolicyAPIGetItemsRequest,
  genericPolicyAPIPostItemRequest: genericPolicyAPIPostItemRequest,
  getVersion: getVersion,
  healthCheck: healthCheck,
  policyAPIPostRequest: policyAPIPostRequest,
  policyAPIGetRequest:  policyAPIGetRequest
};

//--------------------UTILITY METHODS-------------------------------------//
var buildPolicyApiRequestOptions = function(apiPath, requestMethod){
  var corePolicyRequestOptions = {
      hostname: policyConfig.policyAPIHost,
      port: policyConfig.policyAPIPort,
      path: policyConfig.policyAPIEntryPath + apiPath,
      method: requestMethod
  };
  return corePolicyRequestOptions;
};

//adds query params to a corePolicyRequestOption object
var updateRequestWithGetParams = function(corePolicyRequestOptions, params){
  //creating a new version of params so it can be modified here without affecting the caller
  var paramsToSend = {};
  if(params!==null && params !== undefined){
    //cycle through the top level params and if any contain objects then JSON.stringify them so they can be passed correctly in URL
    Object.keys(params).forEach(function(key,index) {
      var paramsValue = params[key];
      //if the parameter value is an object we stringify it. Unless it is an array, querystring library handles arrays for us.
      if(typeof(paramsValue)==='object' && Array.isArray(paramsValue)===false){
        paramsToSend[key] = JSON.stringify(paramsValue);
      }
      else {
        paramsToSend[key] = paramsValue;
      }
    });    
    corePolicyRequestOptions.path += '?'+ querystring.stringify(paramsToSend);
  }
};

//call this to add Content Type and Length headers for POST, PUT, DELETE requests.
var updateRequestWithContentHeaders = function(corePolicyRequestOptions, data){
  if(data===null || data===undefined){
    return;
  }
  corePolicyRequestOptions.headers = {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(data)
  };
};

//reads in data from a response and once response body is fully returned calls the passed in callback with the 
//response and built up response body.
var readResponse = function(response, completedResponseCallback) {
  var responseBody = "";
  response.setEncoding("utf8");
  response.on('data', function(chunk) {
    responseBody += chunk;
  });
  response.on('end', function() {
    completedResponseCallback(response, responseBody);
  });
};

//sends a HTTP request with the specified options. Does not call 'end' and does not write data for POST.
var sendPolicyApiRequest = function(policyApiRequestOptions, policyCallback, errorCallback){
  var policyApiRequest = http.request(policyApiRequestOptions, function(policyApiResponse){
    readResponse(policyApiResponse, policyCallback);
  });
  policyApiRequest.on('error', function(e) {    
    if(errorCallback!==null && errorCallback!==undefined){
      errorCallback(e);
    }
    else{
      logger.error("Error occured making Policy API Request to "+ apiPath +" "+ e);
    }
  });
  return policyApiRequest;
};

var sendPolicyAPIPostRequest = function(postData, policyApiRequestOptions, policyCallback, errorCallback){
  var policyApiRequest = sendPolicyApiRequest(policyApiRequestOptions, policyCallback, errorCallback);
  //finish sending the request, writing the data to request body
  policyApiRequest.end(postData);
  return policyApiRequest;
};

var sendPolicyAPIGetRequest = function(policyApiRequestOptions, policyCallback, errorCallback){
  var policyApiRequest = sendPolicyApiRequest(policyApiRequestOptions, policyCallback, errorCallback);
  //finish sending the request
  policyApiRequest.end();
  return policyApiRequest;
};

//Makes a HTTP request to Core Policy API using the specified path and parameters, calls the passed in callback with the response object
//apiPath                   - path to API method to call
//requestParams             - JSON object of parameters to submit in body of request
//policyAPIRequestCallback  - function to call with response from request, will be 
//                            passed the response and the responseBody as arguments
//error callback            - function to call in case of error event being raised
//Returns the request object created.
function policyAPIPostRequest(apiPath, requestParams, policyAPIRequestCallback, errorCallback){
  var policyApiRequestOptions = buildPolicyApiRequestOptions(apiPath, 
    "POST");
  
  var paramsAsStr = (requestParams !== null && requestParams !== undefined) ? JSON.stringify(requestParams) : "{}";  
  updateRequestWithContentHeaders(policyApiRequestOptions, paramsAsStr);
  
  logger.info("About to issue request with options: " + JSON.stringify(policyApiRequestOptions) + ", body: "+paramsAsStr);
  var policyApiRequest = sendPolicyAPIPostRequest(paramsAsStr, policyApiRequestOptions, policyAPIRequestCallback, errorCallback);
  return policyApiRequest;
}

function policyAPIGetRequest(apiPath, requestParams, policyAPIRequestCallback, errorCallback){
  var policyApiRequestOptions = buildPolicyApiRequestOptions(apiPath, 
    "GET");
  updateRequestWithGetParams(policyApiRequestOptions, requestParams);
    
  logger.info("About to issue request with options: " + JSON.stringify(policyApiRequestOptions));
  var policyApiRequest = sendPolicyAPIGetRequest(policyApiRequestOptions, policyAPIRequestCallback, errorCallback);
  return policyApiRequest;
}

//a convenience method that calls policyAPIGetRequest with a specified policy path and returns the first result of the array of results in the response or null if none. Returns a promise that resolves on success and rejects on failure of the call.
function genericPolicyAPIGetItemRequest(path, params){
  //returns the first item from the array that call returns
  var extractFirstItem = function(policyResponse){
    if(policyResponse.results && policyResponse.results.length > 0){
      return policyResponse.results[0];
    }
    return null;
  };
  var deferredRequest = Q.defer();
  policyAPIGetRequest(path, params,
    promiseHelper.handlePotentialSuccess(deferredRequest, extractFirstItem),
    promiseHelper.handleFailure(deferredRequest)
  );
  return deferredRequest.promise;
}

//a convenience method that calls policyAPIGetRequest with a specified policy path and returns the response. Returns a promise that resolves on success and rejects on failure of the call.
function genericPolicyAPIGetItemsRequest(path, params){
  var deferredRequest = Q.defer();
  policyAPIGetRequest(path, params,
    promiseHelper.handlePotentialSuccess(deferredRequest),
    promiseHelper.handleFailure(deferredRequest)
  );
  return deferredRequest.promise;
}

//a convenience method that calls policyAPIPostRequest with a specified policy path. Returns a promise that resolves on success passing the server response and rejects on failure of the call.
function genericPolicyAPIPostItemRequest(path, params){
  var deferredRequest = Q.defer();
  policyAPIPostRequest(path, params,
    promiseHelper.handlePotentialSuccess(deferredRequest),
    promiseHelper.handleFailure(deferredRequest)
  );
  return deferredRequest.promise;
}

//call the Policy API health check. Returns a promise that resolves with the result.
function healthCheck(){
  return genericPolicyAPIGetItemsRequest('healthcheck', {
    project_id: 'Default' //API requires a project ID to be set but it does not need to correspond to an actual value in the system.
  });
}

//retrieves the version of Policy API that is being contacted. Returns a promise that resolves with the result.
function getVersion(){
  return genericPolicyAPIGetItemsRequest('debug/buildversion', {
    project_id: 'Default' //API requires a project ID to be set but it does not need to correspond to an actual value in the system.
  });
}