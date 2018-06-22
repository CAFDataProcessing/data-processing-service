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
var Q = require('q');
var logger = require('../helpers/loggingHelper.js');
var policyTypeModel = require('./policy_api/policyType.js');
var httpHelper = require('../helpers/httpPromiseHelper.js');
var policyToProcessingHelper = require('../helpers/policyApiToProcessingApiHelper.js');

var defaultNoActionTypeMatchMessage = "Unable to find Action Type with ID: ";

function getNoActionTypeMatchMessage(id){
  return defaultNoActionTypeMatchMessage + id;
}

module.exports.create = function(createActionTypeParams){
  if(createActionTypeParams===null || createActionTypeParams===undefined){
    throw new Error("Must pass an object of properties to use in creating action type.");
  }
  var deferredCreate = Q.defer();
  
  var createPolicyTypeParams = {
    definition: createActionTypeParams.definition,
    description: createActionTypeParams.description,
    internal_name: createActionTypeParams.internal_name,
    name: createActionTypeParams.name
  };
  policyTypeModel.create(createActionTypeParams.project_id, createPolicyTypeParams)
  .then(function(createdPolicyType){
    logger.debug("Created a Policy Type. ID: "+createdPolicyType.id);
    var actionType = policyToProcessingHelper.buildActionTypeFromPolicyType(createdPolicyType);
    deferredCreate.resolve(actionType);
  })
  .fail(function(errorResponse){
    deferredCreate.reject(errorResponse);
  }).done();
  
  return deferredCreate.promise;
};

module.exports.delete = function(deleteActionTypeParams){
  if(deleteActionTypeParams===null || deleteActionTypeParams===undefined){
    throw new Error("Must pass an object of properties to use in deleting action type.");
  }
  var deferredDelete = Q.defer();
  
  policyTypeModel.getWithValidate(deleteActionTypeParams.project_id, deleteActionTypeParams.id, getNoActionTypeMatchMessage(deleteActionTypeParams.id))
  .then(function(retrievedPolicyType){
    logger.debug("Before delete, validated that action type exists with ID: "+retrievedPolicyType.id);
    return policyTypeModel.delete(deleteActionTypeParams.project_id, deleteActionTypeParams.id);
  })
  .then(function(resultOfDelete){
    //handle case where Policy returns 200 after not finding item to delete    
    httpHelper.handleDeleteResponseAndThrow(resultOfDelete);
    deferredDelete.resolve({});
  })
  .fail(function(errorResponse){
    deferredDelete.reject(errorResponse);
  }).done();
  
  return deferredDelete.promise;
};

module.exports.get = function(getActionTypeParams){
  if(getActionTypeParams===null || getActionTypeParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving action type.");
  }
  var deferredGet = Q.defer();
  
  policyTypeModel.getWithValidate(getActionTypeParams.project_id, getActionTypeParams.id, getNoActionTypeMatchMessage(getActionTypeParams.id))
  .then(function(retrievedPolicyType){
    logger.debug("Retrieved Policy Type with ID: "+retrievedPolicyType.id);
    var actionType = policyToProcessingHelper.buildActionTypeFromPolicyType(retrievedPolicyType);
    deferredGet.resolve(actionType);
  })
  .fail(function(errorResponse){
    deferredGet.reject(errorResponse);
  }).done();
  
  return deferredGet.promise;
};

module.exports.getActionTypes = function(getActionTypesParams){
  if(getActionTypesParams===null || getActionTypesParams===undefined){
    throw new Error("Must pass an object of properties to use in retrieving action types.");
  }
  var deferredGet = Q.defer();
  policyTypeModel.getTypes(getActionTypesParams.project_id, getActionTypesParams.pageNum, getActionTypesParams.pageSize)
  .then(function(returnedPolicyTypes){
    var actionTypes = [];
    for(var policyType of returnedPolicyTypes.results){
      actionTypes.push(policyToProcessingHelper.buildActionTypeFromPolicyType(policyType));
    }
    deferredGet.resolve({
      totalHits: returnedPolicyTypes.totalhits,
      actionTypes: actionTypes
    });
  })
  .fail(function(errorResponse){
    deferredGet.reject(errorResponse);
  }).done();
  
  return deferredGet.promise;
};

module.exports.update = function(updateActionTypeParams){
  if(updateActionTypeParams===null || updateActionTypeParams===undefined){
    throw new Error("Must pass an object of properties to use in updating action type.");
  }
  var deferredUpdate = Q.defer();
  
  
  policyTypeModel.getWithValidate(updateActionTypeParams.project_id, updateActionTypeParams.id, getNoActionTypeMatchMessage(updateActionTypeParams.id))
  .then(function(retrievedPolicyType){
    logger.debug("Validated that action type exists before updating. ID: "+updateActionTypeParams.id);
    var updatePolicyTypeParams = {
      definition: updateActionTypeParams.definition,
      description: updateActionTypeParams.description,
      id: updateActionTypeParams.id,
      internal_name: updateActionTypeParams.internal_name,
      name: updateActionTypeParams.name
    };
    return policyTypeModel.update(updateActionTypeParams.project_id, updatePolicyTypeParams);
  })
  .then(function(updatedPolicyType){
    logger.debug("Updated Policy Type with ID: "+updatedPolicyType.id);
    var actionType = policyToProcessingHelper.buildActionTypeFromPolicyType(updatedPolicyType);
    deferredUpdate.resolve(actionType);
  })
  .fail(function(errorResponse){
    deferredUpdate.reject(errorResponse);
  }).done();
  
  return deferredUpdate.promise;
};