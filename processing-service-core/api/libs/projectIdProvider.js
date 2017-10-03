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
//returns the active implementation to use in retrieving project ID for request
//default authorization providers available

var projectIdHeader = require('./projectIdHeader.js');

//Peforms authorization using currently configured implementation using the passed parameters
module.exports.getProjectId = function(options, request){
  //only this default implementation for now so just returning it.
  return projectIdHeader(options, request);
};