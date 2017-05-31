#!/bin/bash
#
# Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


echo "Installing Classification database"
java \
 -Dapi.mode=direct \
 -Dapi.direct.repository=hibernate \
 -Dhibernate.connectionstring=jdbc:postgresql://${CAF_POSTGRES_HOST:-127.0.0.1}:${CAF_POSTGRES_PORT:-5432}/'<dbname>' \
 -Dhibernate.user=$POSTGRES_USER \
 -Dhibernate.password=$POSTGRES_PASSWORD \
 -Dhibernate.databasename=${CAF_CLASSIFICATION_DB_NAME:-classification} \
 -jar /corepolicy-database.jar -c $@

exitValue=$?

if [ $exitValue != 0 ]
then
echo "Failed to install Classification database"
exit $exitValue
fi

echo "Installed Classification database"
