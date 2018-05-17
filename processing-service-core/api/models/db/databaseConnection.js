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
// exports a database connection that may be utilized across multiple classes

const Sequelize = require('sequelize');
const Q = require('q');
var logger = require('../../helpers/loggingHelper.js');
const databaseConfig = require('../../helpers/processingDatabaseConfigHelper.js');

function healthCheck() {
    var deferredHealthCheck = Q.defer();
    databaseDefinition.authenticate()
        .then(function() {
            logger.debug('Database connection has been established successfully.');
            deferredHealthCheck.resolve(true);
        })
        .catch(function(err) {
            logger.error('Unable to connect to the database: ' + err);
            deferredHealthCheck.reject('Unable to connect to the database');
        });
    return deferredHealthCheck.promise;
}

const databaseDefinition = new Sequelize(databaseConfig.name, databaseConfig.username, databaseConfig.password,
    {
        dialect: 'postgres',
        host: databaseConfig.host,
        logging: logger.debug,
        operatorsAliases: false,
        port: databaseConfig.port
    }
);

//healthCheck();

module.exports = {
    definition: databaseDefinition,
    healthCheck: healthCheck
};
