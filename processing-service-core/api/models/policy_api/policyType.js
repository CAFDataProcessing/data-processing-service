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
//methods for interacting with Policy Types via Policy API
var Q = require('q');
var NodeCache = require( "node-cache" );
var appConfig = require('../../helpers/dataProcessingServiceConfigHelper.js');
var policyHttpHelper = require('../../helpers/policyHttpHelper.js');
var promiseHelper = require('../../helpers/httpPromiseHelper.js');
var pagingHelper = require('../../helpers/pagingHelper.js');
var apiErrorFactory = require('../errors/apiErrorFactory.js');

module.exports = {
  create: create,
  delete: deleteType,
  get: get,
  getWithValidate: getWithValidate,
  getTypes: getTypes,
  getTypesByIds: getTypesByIds,
  update: update  
};

var defaultNoMatchMessage = "Unable to find Policy Type with ID: ";

//setting up caches
var policyTypeCache = new NodeCache({
  stdTTL: appConfig.cacheDuration
});

var buildCacheKey = function(projectId, typeId){
  return projectId + typeId;
};

//returns a params object with common parameters for Policy Type. Takes in a project ID and uses that in the params.
var getDefaultParams = function(projectId){
  return {
    project_id: projectId,
    type: "policy_type"
  };
};

function create(projectId, policyType){
  var createPolicyTypeParams = getDefaultParams(projectId);
  createPolicyTypeParams.description = policyType.description;
  createPolicyTypeParams.name = policyType.name;
  
  createPolicyTypeParams.additional = {
    conflict_resolution_mode: policyType.conflict_resolution_mode,
    definition: policyType.definition,
    short_name: policyType.internal_name
  };
  var createPromise = policyHttpHelper.genericPolicyAPIPostItemRequest("policy/create", createPolicyTypeParams);
  createPromise.then(function(result){
    policyTypeCache.set(buildCacheKey(projectId, result.id), result);
  });
  return createPromise;
}

function update(projectId, policyType){
  var updatePolicyTypeParams = getDefaultParams(projectId);
  updatePolicyTypeParams.id = policyType.id;
  updatePolicyTypeParams.description = policyType.description;
  updatePolicyTypeParams.name = policyType.name;
  updatePolicyTypeParams.additional = {
    conflict_resolution_mode: policyType.conflict_resolution_mode,
    definition: policyType.definition,
    short_name: policyType.internal_name
  };
  var updatePromise = policyHttpHelper.genericPolicyAPIPostItemRequest("policy/update", updatePolicyTypeParams);
  updatePromise.then(function(result){
    policyTypeCache.set(buildCacheKey(projectId, result.id), result);
  });
  return updatePromise;
}

function deleteType(projectId, policyTypeId){
  var deletePolicyTypeParams = getDefaultParams(projectId);
  deletePolicyTypeParams.id = policyTypeId;
  var deletePromise = policyHttpHelper.genericPolicyAPIPostItemRequest("policy/delete", deletePolicyTypeParams);
  deletePromise.then(function(result){
    policyTypeCache.del(buildCacheKey(projectId, policyTypeId), result);
  });
  return deletePromise;
}

function get(projectId, policyTypeId){
  //if this type is already in the cache then return it (wrapped in a promise)
  var cachedType = policyTypeCache.get(buildCacheKey(projectId, policyTypeId));
  if(cachedType!==undefined){
    return Q(cachedType);
  }
  
  var getPolicyTypeParams = getDefaultParams(projectId);
  getPolicyTypeParams.id = policyTypeId;
  
  var getTypePromise = policyHttpHelper.genericPolicyAPIGetItemRequest("policy/retrieve", getPolicyTypeParams);
  getTypePromise.then(function(returnedPolicyType){
    policyTypeCache.set(buildCacheKey(projectId, policyTypeId), returnedPolicyType);
  });
  return getTypePromise;
}

function getWithValidate(projectId, policyTypeId, noMatchMessage){
  var validatedPromise = Q.defer();
  var getPromise = get(projectId, policyTypeId);
  getPromise.then(function(retrievedType){
    if(retrievedType===null || retrievedType===undefined){
      if(noMatchMessage===null || noMatchMessage===undefined){
        validatedPromise.reject(apiErrorFactory.createNotFoundError(defaultNoMatchMessage + policyTypeId));
        return;
      }
      validatedPromise.reject(apiErrorFactory.createNotFoundError(noMatchMessage + policyTypeId));
      return;
    }
    validatedPromise.resolve(retrievedType);
  })
  .fail(function(errorResponse){
  //throwing a more helpful error here if indication is that the Type ID was wrong.
    if(errorResponse.response &&
      errorResponse.response.message === "Could not retrieve Policy Type"){
      var returnMessage;
      if(noMatchMessage===undefined || noMatchMessage === null){
        returnMessage = defaultNoMatchMessage +lexiconId;
      }
      else {
        returnMessage = noMatchMessage;
      }
      validatedPromise.reject(apiErrorFactory.createNotFoundError(returnMessage));
    }
    else{
      validatedPromise.reject(errorResponse);
    }
  }).done();
  return validatedPromise.promise;
}

function getTypes(projectId, pageNum, pageSize){
  var pageOptions = pagingHelper.getValidatedPagingParams(pageNum, pageSize);
  var getTypesParams = getDefaultParams(projectId);
  getTypesParams.max_page_results = pageOptions.pageSize;
  getTypesParams.start = pageOptions.start;
  
  var getTypesPromise = policyHttpHelper.genericPolicyAPIGetItemsRequest("policy/retrieve", getTypesParams);
  getTypesPromise.then(function(returnedPolicyTypes){
    //add these retrieved entries to the cache
    for(var returnedType of returnedPolicyTypes.results){
      policyTypeCache.set(buildCacheKey(projectId, returnedType.id), returnedType);
    }
  });  
  return getTypesPromise;
}

function getTypesByIds(projectId, params){
  var getTypesParams = getDefaultParams(projectId);
  
  var idsToRequest = [];
  var typesFromCache = [];
  
  //see if we have any of these Policy Types already in the cache to avoid requesting them again
  for(var typeId of params.ids){
    var typeFromCache = policyTypeCache.get(buildCacheKey(projectId, typeId));
    if(typeFromCache===undefined){
      idsToRequest.push(typeId);
    }
    else {
      typesFromCache.push(typeFromCache);
    }
  }
  //if we have all the ids in the cache then construct a response without going to the API
  if(idsToRequest.length ===0){
    return Q({
      totalhits: typesFromCache.length,
      results: typesFromCache
    });
  }
  getTypesParams.id = idsToRequest;
  
  var deferredGetTypes = Q.defer();
  policyHttpHelper.genericPolicyAPIGetItemsRequest("policy/retrieve", getTypesParams)
  .then(function(returnedTypes){
    //add these retrieved entries to the cache
    for(var returnedType of returnedTypes.results){
      policyTypeCache.set(buildCacheKey(projectId, returnedType.id), returnedType);
    }
    //add those that were already in the cache to the results
    returnedTypes.totalhits += typesFromCache.length;
    returnedTypes.results = returnedTypes.results.concat(typesFromCache);    
    
    deferredGetTypes.resolve(returnedTypes);
  })
  .fail(function(errorResponse){
    deferredGetTypes.reject(errorResponse);
  }).done();
  return deferredGetTypes.promise;
}