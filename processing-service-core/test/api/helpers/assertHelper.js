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
//Wraps the common NodeJS assert methods, adding additional functionality where necessary
var assertLibrary = require("assert");

module.exports = assertFunction;

var getActualAndExpectedText = function(actual, expected, message){
  var output = "\r\nExpected: ";
  output += typeof(expected==='object') ? JSON.stringify(expected) : expected;
  output += "\r\nActual: ";
  output += typeof(actual==='object') ? JSON.stringify(actual) : actual;
  return message + output;
};

function failFunction(actual, expected, message){
  assertLibrary.fail(actual, expected, getActualAndExpectedText(actual, expected, message));
}

function assertFunction(expression, message){
  assertLibrary(expression, message);
}
//adds expected and actual values to the message.
function equalFunction(actual, expected, message){
  assertLibrary.equal(actual, expected, getActualAndExpectedText(actual, expected, message));
}
module.exports.assert = assertFunction;
module.exports.equal = equalFunction;
module.exports.fail = failFunction;