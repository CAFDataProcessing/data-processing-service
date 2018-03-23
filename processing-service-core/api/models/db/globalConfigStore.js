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
'use strict';

var Q = require('q');
var logger = require('../../helpers/loggingHelper.js');
var globalConfig = [{"ee.grammarMap": "{\"pii.xml\": []}"}];
var apiErrorFactory = require('../errors/apiErrorFactory.js');

module.exports = {
    getGlobalConfig: getGlobalConfig,
    getGlobalConfigs: getGlobalConfigs
};

function getGlobalConfig(key) {
    var defaultPromise = Q.defer();
    for (var i = 0; i < globalConfig.length; i++) {
        for (var keyValue in globalConfig[i]) {
            if (keyValue === key) {
                defaultPromise.resolve(globalConfig[i]);
                return defaultPromise.promise;
            }
        }
    }
    defaultPromise.reject(apiErrorFactory.createNotFoundError("No configuration was found matching this key"));
    return defaultPromise.promise;

}

function getGlobalConfigs() {
    var defaultPromise = Q.defer();
    if (globalConfig !== undefined) {
        defaultPromise.resolve(globalConfig);
    } else {
        defaultPromise.resolve([]);
    }
    return defaultPromise.promise;
}