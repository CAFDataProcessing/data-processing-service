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
//represents the types of errors that can occur in the API logic.
module.exports = {
  /**
   * Describes a general database failure where no more specific error type describes the failure.
   */
  DATABASE_UNKNOWN_ERROR: 'DATABASE_UNKNOWN_ERROR',
  ITEM_NOT_FOUND: 'ITEM_NOT_FOUND',
  METHOD_NOT_ALLOWED: 'METHOD_NOT_ALLOWED',
  UNKNOWN: 'UNKNOWN'
};
