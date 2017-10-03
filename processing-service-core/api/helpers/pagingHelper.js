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
//Utility class with useful methods to support page requests.

var pageSizeDefault = 100;
var startDefault = 1;

//takes a page number and page size and returns object with the appropriate start index to use in paging and
//either the page size passed or the default page size if the passed value was invalid
module.exports.getValidatedPagingParams = function(pageNum, pageSize){
  var pageOptions = {};
  pageOptions.start = startDefault;
  pageOptions.pageSize = pageSizeDefault;
  
  if(pageSize!==undefined && pageSize !== null && pageSize >= 0){
    pageOptions.pageSize = pageSize;
  }
  if(pageNum!==undefined && pageNum !== null){
    pageOptions.start = pageNum === 1 || pageNum === 0 ? 1 : (pageNum-1) * pageOptions.pageSize + 1;
  }
  return pageOptions;
};

//takes an array and a pagingParams object and returns only elements from the array that meet the paging specified.
module.exports.buildArrayFromPagingParams = function(fullArray, pagingParams){
  var arrayToReturn = [];
  var itemCounter = 0;
  for(var arrayItem of fullArray){
    itemCounter++;
    if(itemCounter < pagingParams.start){
      continue;
    }
    arrayToReturn.push(arrayItem);
    
    //check if we have reached page size limit, if so then no need to add any more
    if(arrayToReturn.length === pagingParams.pageSize){
      break;
    }
  }
  return arrayToReturn;
}