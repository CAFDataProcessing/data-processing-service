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
var Q = require('q');
var policyApiHelper = require('../../helpers/policyApiHelpers.js');
var pagingHelper = require('../../helpers/pagingHelper.js');
var apiErrorFactory = require('../errors/apiErrorFactory.js');

//returns a params object with common parameters for Collection. Takes in a project ID and uses that in the params.
var getDefaultParams = function(projectId){
  return {
    project_id: projectId,
    type: "collection"
  };
};

//get a collection with the specified ID. 'include_condition' and 'include_children' can optionally be set. Returns a promise.
module.exports = {
  create: create,
  delete: deleteCollection,
  deleteAll: deleteAll,
  get: get,
  getCollections: getCollections,
  getCollectionsByIds: getCollectionsByIds,
  update: update,
  validateCollectionExists: validateCollectionExists
};

function get(projectId, collectionId, include_condition, include_children){
  var getCollectionParams = getDefaultParams(projectId);
  getCollectionParams.id = collectionId;
  getCollectionParams.additional = {};
  if(include_condition){    
    getCollectionParams.additional.include_condition = true;
  }
  if(include_children){
    getCollectionParams.additional.include_children = true;
  }
  
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemRequest("classification/retrieve", getCollectionParams);
}

//gets collections in the system. Supports paging. Will not return detail about policyIds or conditions on Collection. Returns a promise.
function getCollections(projectId, pageNum, pageSize){
  var pageOptions = pagingHelper.getValidatedPagingParams(pageNum, pageSize);
  var getCollectionsParams = getDefaultParams(projectId);
  getCollectionsParams.max_page_results = pageOptions.pageSize;
  getCollectionsParams.start = pageOptions.start;
    
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemsRequest("classification/retrieve", getCollectionsParams);
}

//gets collections with IDs matching those in the 'ids' property on the params object passed. Returns a promise.
//If includeCondition is set on the params object then details about the condition assigned to Collection will be returned on each Collection object. Defaults to false.
//If includeChildren is set on the params object then details about the children of the condition assigned to the Collection will be returned on each Collection object. Defaults to false.
//Will return details about policyIds on a Collection
function getCollectionsByIds(projectId, params){
  var getCollectionsParams = getDefaultParams(projectId);

  getCollectionsParams.id = params.ids;

  getCollectionsParams.additional = {
    include_condition: params.includeCondition === undefined ? false : params.includeCondition,
    include_children: params.includeChildren === undefined ? false : params.includeChildren
  };
  return policyApiHelper.HttpHelper.genericPolicyAPIGetItemsRequest("classification/retrieve", getCollectionsParams);
}

//create a collection using the provided parameter object. Returns a promise.
function create(projectId, collection){
  var createCollectionParams = getDefaultParams(projectId);
  createCollectionParams.additional = {
    condition: collection.condition,
    policy_ids: collection.policyIds
  };
  createCollectionParams.description = collection.description;
  createCollectionParams.name = collection.name;

  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/create", createCollectionParams);
}
//update a collection using the provided parameter object. Returns a promise.
function update(projectId, collection){
  var updateCollectionParams = getDefaultParams(projectId);
  
  //note that undefined or null for policy_ids or condition will cause no change in them. To empty them 
  updateCollectionParams.additional = {
    policy_ids: collection.policyIds,
    condition: collection.condition
  };
  updateCollectionParams.description = collection.description;
  updateCollectionParams.id = collection.id;
  updateCollectionParams.name = collection.name;
  
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/update", updateCollectionParams);
}
function deleteCollection(projectId, collectionId){
  var deleteCollectionParams = getDefaultParams(projectId);
  deleteCollectionParams.id = collectionId;
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/delete", deleteCollectionParams);
}
//delete all Collections specified in the passed array. Returns a promise.
function deleteAll(projectId, collectionIds){
  var deleteCollectionParams = getDefaultParams(projectId);
  deleteCollectionParams.id = collectionIds;
  return policyApiHelper.HttpHelper.genericPolicyAPIPostItemRequest("classification/delete", deleteCollectionParams);
}

//Retuns a promise to check that a given Collection exists. Resolved result will be the retrieved Collection
function validateCollectionExists(projectId, collectionId, includeAllConditions, noMatchMessage){
  var validatedCollectionPromise = Q.defer();
  get(projectId, collectionId, includeAllConditions, includeAllConditions)
  .then(function(retrievedCollection){
    validatedCollectionPromise.resolve(retrievedCollection);
  })
  .fail(function(errorResponse){
    //throwing a more helpful error here if indication is that the Collection ID was wrong.
    if(errorResponse.response && errorResponse.response.reason === "Could not return items for all ids"){
      if(noMatchMessage===undefined || noMatchMessage === null){
        noMatchMessage = "Unable to find Collection with ID: "+collectionId;
      }
      validatedCollectionPromise.reject(apiErrorFactory.createNotFoundError(noMatchMessage));
    }
    else{
      validatedCollectionPromise.reject(apiErrorFactory.createNotFoundError(errorResponse));
    }
  }).done();
  return validatedCollectionPromise.promise;
}