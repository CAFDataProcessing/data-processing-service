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
//Registers paths in the NodeJS application that relate to the swagger contract file itself.
var express = require('express');

module.exports = {
  register: register
};

//register paths with app instance
function register(config, app, swaggerExpress){
  //register path to return the swagger contract
  app.use(swaggerExpress.runner.swagger.basePath + '/swagger.yaml', function(request, response, next){
    response.sendFile(config.appRoot + '/api/swagger/swagger.yaml');
  });
  
  //register path to return swagger UI
  app.use('/data-processing-ui', function(request, response, next){
    response.sendFile(config.appRoot + '/ui/index.html');
  });
  
  //make the UI assets available at root level
  app.use(express.static(config.appRoot + '/ui'));
}