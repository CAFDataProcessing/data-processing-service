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
// defines the structure representing the globalConfig table in the database
const Sequelize = require("sequelize");

var tableDefinition = {
    key: {
        allowNull: false,
        field: "key",
        primaryKey: true,
        type: Sequelize.STRING(255)
    },
    default: {
        allowNull: false,
        field: "default",
        type: Sequelize.TEXT
    },
    description: {
        field: "description",
        type: Sequelize.TEXT
    }
};

module.exports = {
    definition: tableDefinition,
    name: "globalConfig",
    tableName: "tbl_global_config"
};
