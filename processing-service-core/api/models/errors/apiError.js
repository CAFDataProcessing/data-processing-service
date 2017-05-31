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
//internal error type to represent error situations in the processing API. Such as not finding an item using an ID specified.
var apiErrorTypes = require('./apiErrorTypes.js');

module.exports = ApiError;

function ApiError(message, type){
  this.message = message;
  this.type = type || apiErrorTypes.UNKNOWN;
}