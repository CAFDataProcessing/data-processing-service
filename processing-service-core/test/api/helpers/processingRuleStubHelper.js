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
//holds stub functions for processing rule models
var assert = require("./assertHelper.js");
var Q = require('q');

//get test data
var processingData = require("../data/processing.js");
var workflows = processingData.workflows;
var workflowEntries = processingData.workflowEntries;
var colSeqs = processingData.collectionSequences;

module.exports.getWorkflowsWithCollectionSequenceIdStubFunction = function(expectedProject_id, expectedColSeqId){
  return function(project_id, colSeqId){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to getWorkflowsWithCollectionSequenceId to be that set in test.");
    assert.equal(colSeqId, expectedColSeqId, "Expecting workflowId passed to getWorkflowsWithCollectionSequenceId to be that set in test.");
    
    return Q.fcall(function(){
      return workflows[1];
    });
  };
};

module.exports.getWorkflowEntriesStubFunction =  function(expectedProject_id, expectedWorkflowId, expectedPageNum, expectedPageSize) {
  return function(project_id, workflowId, pageNum, pageSize){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to getWorkflowEntriesByWorkflowId to be that set in test.");
    assert.equal(workflowId, expectedWorkflowId, "Expecting workflowId passed to getWorkflowEntriesByWorkflowId to be that set in test.");
    assert.equal(pageNum, expectedPageNum, "Expecting pageNum to be passed as set in test.");
    assert.equal(pageSize, expectedPageSize, "Expecting pageSize to be passed as set in test.");
    
    //return a promise that passes the workflow entries back
    return Q.fcall(function(){
      return workflowEntries;
    });
  };
};

module.exports.getColSeqsStubFunction = function(expectedProject_id, alreadyRequestedIds){
  return function(project_id, colSeqId){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to set for test.");
    
    //verify this ID wasn't already requested
    if(alreadyRequestedIds.indexOf(colSeqId)===-1){
      alreadyRequestedIds.push(colSeqId);
    }
    else{
      throw new Error("A get collection sequence call was made for a sequence that had already been requested. ID: "+colSeqId);
    }
    
    //return a promise that passes the collection sequence back
    return Q.fcall(function(){
      var colSeq = colSeqs[colSeqId];
      if(colSeq!==undefined){
        //converting to string and back to object to avoid passing reference
        return JSON.parse(JSON.stringify(colSeq.results[0]));
      }              
      throw new Error("request made for unknown collection sequence, ID: "+colSeqId);
    });
  };  
};

module.exports.getWorkflowByIDStubFunction = function(expectedProject_id, expectedWorkflowId){
  return function(project_id, workflowId){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to set for test.");
    assert.equal(workflowId, expectedWorkflowId, "Expecting workflowId passed to getWorkflowById to be that set in test.");
    
    //return a promise that passes the workflow back
    return Q.fcall(function(){
      var workflow = workflows[workflowId];
      if(workflow!==undefined){
        //converting to string and back to object to avoid passing reference
        return JSON.parse(JSON.stringify(workflow.results[0]));
      }              
      throw new Error("request made for unknown workflow, ID: "+colSeqId);
    });
  };
};

module.exports.updateWorkflowStubFunction = function(expectedProject_id, expectedWorkflow){
  return function(project_id, updatedWorkflow){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to set for test.");
    assert(updatedWorkflow!==null && updatedWorkflow!==undefined, "Expecting workflow passed to update to not be null or undefined.");
    
    assert.equal(updatedWorkflow.name, expectedWorkflow.name, "Workflow name passed to update should be as expected.");
    assert.equal(updatedWorkflow.description, expectedWorkflow.description, "Workflow description passed to update should be as expected.");
    assert.equal(updatedWorkflow.id, expectedWorkflow.id, "Workflow id passed to update should be as expected.");
    
    //check that sequence entries match on both workflows
    if(expectedWorkflow.additional.sequence_entries===undefined){
      assert(updatedWorkflow.additional.sequence_entries!==null ||
          updatedWorkflow.additional.sequence_entries!==undefined, "'additional' prop should not be set on Workflow.");
      return;
    }
    for(var expectedEntry of expectedWorkflow.additional.sequence_entries){
      var updatedEntry = updatedWorkflow.additional.sequence_entries.find(function(entryToCheck){
        if(entryToCheck.collection_sequence_id===null || entryToCheck.collection_sequence_id===undefined){
          throw new Error("Workflow Sequence Entry returned had no 'collection_sequence_id' property.");
        }
        return entryToCheck.collection_sequence_id===expectedEntry.collection_sequence_id;
      });      
      assert(updatedEntry!==null && updatedEntry!==undefined, "Expecting an entry returned with collection_sequence_id: "+expectedEntry.collection_sequence_id);
      //check all props on this entry
      assert.equal(updatedEntry.order, expectedEntry.order, "order on entry should be as expected.");
      assert.equal(updatedEntry.sequence_workflow_id, expectedEntry.sequence_workflow_id, "sequence_workflow_id on entry should be as expected");
    }
    return Q.fcall(function(){
      return updatedWorkflow;
    });
  };
};

//Returns a function that stubs the behaviour of create for collection sequence in test.
//expected_project_id     - the project ID the function is expected to be called with.
//expectedColSeq          - object describing the properties on the collection sequence that we expect to be passed.
//createdSequencesCounter - reference to a counter that should be incremented and used to return an ID for new sequence
module.exports.createColSeqStubFunction = function(expectedProject_id, expectedColSeq, createdSequencesCounter){  
  return function(project_id, colSeqToCreate){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to set for test.");
    
    assert(colSeqToCreate!==null && colSeqToCreate!==undefined, "Expecting collection sequence passed to create to not be null or undefined.");
    
    assert.equal(colSeqToCreate.name, expectedColSeq.name, "Name passed when creating col.seq. should be that originally passed to method.");
    assert.equal(colSeqToCreate.description, expectedColSeq.description, "Description passed when creating col.seq. should be that originally passed to method.");
    
    //return a promise that passes the col seq created back
    return Q.fcall(function(){
      var createdSequence = {
        description: expectedColSeq.description,
        id: createdSequencesCounter + 1,
        name: expectedColSeq.name        
      };
      //TODO add 'additional' when required
      createdSequencesCounter++;
      return createdSequence;
    });
  };
};

module.exports.updateColSeqStubFunction = function(expectedProject_id, expectedColSeq){
  return function(project_id, colSeqToUpdate){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to set for test.");
    
    assert(colSeqToUpdate!==null && colSeqToUpdate!==undefined, "Expecting collection sequence passed to update to not be null or undefined.");
    
    assert.equal(colSeqToUpdate.id, expectedColSeq.id, "Name passed when updating col.seq. should be that originally passed to method.");
    assert.equal(colSeqToUpdate.name, expectedColSeq.name, "Name passed when updating col.seq. should be that originally passed to method.");
    assert.equal(colSeqToUpdate.description, expectedColSeq.description, "Description passed when updating col.seq. should be that originally passed to method.");
    
    //return a promise that passes the col seq updated back
    return Q.fcall(function(){
      var updatedSequence = {
        description: expectedColSeq.description,
        id: expectedColSeq.id,
        name: expectedColSeq.name        
      };
      return updatedSequence;
    });
  };
};

module.exports.deleteColSeqStubFunction = function(expectedProject_id, expectedColSeqId){
  return function(project_id, colSeqId){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to that set for test.");
    
    assert(colSeqId!==null && colSeqId!==undefined, "Expecting collection sequence ID passed to delete to not be null or undefined.");
    
    assert.equal(colSeqId.id, expectedColSeqId.id, "ID passed when deleting col.seq. should be that originally passed to method.");
    
    //return a promise that passes the col seq updated back
    return Q.fcall(function(){
      return {};
    });
  };
};

module.exports.getColSeqStubFunction = function(expectedProject_id, expectedColSeqId){
  return function(project_id, colSeqId){
    assert.equal(project_id, expectedProject_id, "Expecting project_id passed to be that set for test.");
    
    assert(colSeqId!==null && colSeqId!==undefined, "Expecting collection sequence ID passed to get to not be null or undefined.");
    
    assert.equal(colSeqId.id, expectedColSeqId.id, "ID passed when getting col.seq. should be that originally passed to method.");
    
    //return a promise that passes the relevant col seq.
    return Q.fcall(function(){
      return colSeqs[colSeqId].results[0];
    });
  };
};