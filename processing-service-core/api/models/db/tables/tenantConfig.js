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
// defines the structure representing the tenantConfig table in the database
const Sequelize = require("sequelize");
const globalConfigTableDetails = require('./globalConfig.js')

var tableDefinition = {
    tenantId: {
        allowNull: false,
        field: "tenant_id",
        primaryKey: true,
        type: Sequelize.STRING(40)
    },
    key: {
        allowNull: false,
        field: "key",
        primaryKey: true,
        references: {
            key: globalConfigTableDetails.definition.key.field,
            model: globalConfigTableDetails.tableName
        },
        type: Sequelize.STRING(255)
    },
    value: {
        allowNull: false,
        field: "value",
        type: Sequelize.TEXT
    }
};

module.exports = {
    definition: tableDefinition,
    name: "tenantConfig",
    tableName: "tbl_tenant_config"
};
