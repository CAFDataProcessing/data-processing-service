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
//this fitting will wrap any numbers sent in request in BigNumber object wrappers so that no precision is lost in issuing request
var JSONbig = require('json-bigint');
var BigNumber = require('bignumber.js');

//override global JSON object with version that understands BigNumber and will convert it to correct string representation when issuing request.
GLOBAL.JSON = JSONbig;


module.exports = function create(fittingDef, pipes) {
  //the swagger validation logic uses z-schema to validate parameters. We need this validation to recognise BigNumber as type 'integer' so that the request is treated as valid after the JSON is parsed and the large numbers become BigNumber objects.
  var Utils = require('z-schema/src/Utils.js');
  if(Utils==null){
    throw new Error("Error initialising handle64BitNumbers fitting. Could not find 'Utils' library in the cache of modules in order to override its behaviour for 64-bit integer support.");
  }
  var originalWhatIs = Utils.whatIs;
  Utils.whatIs = function(what){
    //adding support for BigNumber to be detected as 'integer' type.
    if(what instanceof BigNumber){
      return 'integer';
    }
    //fall back to original 'whatIs' implementation in other cases.
    return originalWhatIs(what);
  };
  
  return function updateRequestWithBigNumberObjects(context, next) { 
    
    for(var requestParameterKey of Object.keys(context.request.swagger.params)){
      handleParameter(context.request.swagger.params[requestParameterKey]);
    }    
    return next();
  };
};

function handleParameter(requestParameter){  
  //only perform this on params with schema set to format of int64 and type integer/number
  if(requestParameter.schema==undefined || requestParameter.schema.type == undefined || requestParameter.schema.format !== 'int64' || !(requestParameter.schema.type === 'integer' || requestParameter.schema.format === 'number')){
    return;
  }
  
  if(requestParameter.value.toString() === requestParameter.originalValue ){
      return;
  }
  //if they don't match that indicates loss of precision, set value to BigNumber representation instead based on original value
  requestParameter.value = new BigNumber(requestParameter.originalValue);
}
