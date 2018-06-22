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
//factory offering convinience methods to create API errors.

var ApiError = require('../errors/apiError.js');
var apiErrorTypes = require('../errors/apiErrorTypes.js');

module.exports = {
  createDatabaseUnknownError: createDatabaseUnknownError,
  createMethodNotAllowedError: createMethodNotAllowedError,
  createError: createError,
  createNotFoundError: createNotFoundError  
}

function createDatabaseUnknownError(message) {
  return new ApiError(message, apiErrorTypes.DATABASE_UNKNOWN_ERROR);
}

function createMethodNotAllowedError(message) {
  return new ApiError(message, apiErrorTypes.METHOD_NOT_ALLOWED);
}

function createNotFoundError(message){
  return new ApiError(message, apiErrorTypes.ITEM_NOT_FOUND);
}

function createError(message, type){
  if(type===undefined || type === null){
    return new ApiError(message, apiErrorTypes.UNKNOWN);
  }
  return new ApiError(message, type);
}
