/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
//unit tests for workflow.js in models
var assert = require("../helpers/assertHelper.js");
var sinon = require("sinon");

var workflowModel = require('../../../api/models/policy_api/workflow.js');

//get test data
var processingData = require("../data/processing.js");
var workflows = processingData.workflows;

//resolves timeout issue when using sinon.test
sinon.config = {
  useFakeTimers: false
};

describe('models - workflow', function(){
  describe('insertCollectionSequenceIntoWorkflowEntries', function(){
    describe('No priority passed', function(){
      it('Should add entry with no priority passed to workflow with existing entries as one more than existing highest priority on Workflow.', sinon.test(function(done){
        var testColSeqID = 3435353;
        var test_workflow = JSON.parse(JSON.stringify(workflows[1].results[0]));
        var entriesLengthPreCall = test_workflow.additional.sequence_entries.length;
        
        var insertedEntry = workflowModel.insertCollectionSequenceIntoWorkflowEntries(test_workflow, testColSeqID);
        
        //check that inserted entry has specified 
        assert(test_workflow.additional.sequence_entries.length === entriesLengthPreCall + 1, "Entries number should increase by 1" );
        assert(insertedEntry!==null && insertedEntry!==undefined, "Inserted entry should not be null or undefined");
        assert.equal(insertedEntry.collection_sequence_id, testColSeqID, "ID of inserted entry should be that of ID passed into method.");
        assert.equal(insertedEntry.order, entriesLengthPreCall, "Expecting order to have been set to the length of the entries originally (due to 0 based indexing).");
        assert.equal(insertedEntry.sequence_workflow_id, test_workflow.id, "'sequence_workflow_id' property should be set to the id of the workflow entry added to.");
        
        done();
      }));
    });
    describe('Priority passed.', function(){
      it('Should add entry with priority passed to workflow with existing entries, incrementing the priority of existing entries that are greater than or equal to the priority passed. Priority set to middle of existing entries', sinon.test(function(done){
        var testColSeqID = 3435353;
        var testPriority = 3;
        var test_workflow = JSON.parse(JSON.stringify(workflows[1].results[0]));
        var entriesLengthPreCall = test_workflow.additional.sequence_entries.length;
        
        var insertedEntry = workflowModel.insertCollectionSequenceIntoWorkflowEntries(test_workflow, testColSeqID,
          testPriority);
        
        //check that inserted entry has specified 
        assert(test_workflow.additional.sequence_entries.length === entriesLengthPreCall + 1, "Entries number should increase by 1" );
        assert(insertedEntry!==null && insertedEntry!==undefined, "Inserted entry should not be null or undefined");
        assert.equal(insertedEntry.collection_sequence_id, testColSeqID, "ID of inserted entry should be that of ID passed into method.");
        assert.equal(insertedEntry.order, testPriority, "Expecting order to have been set to the the priority passed.");
        assert.equal(insertedEntry.sequence_workflow_id, test_workflow.id, "'sequence_workflow_id' property should be set to the id of the workflow entry added to.");
        
        //check that entries already on the workflow were incremented
        var originalWorkflow = workflows[1].results[0];
        for(var originalEntry of originalWorkflow.additional.sequence_entries){
          var matchedEntry = null;
          for(var testEntry of test_workflow.additional.sequence_entries){
            if(testEntry.collection_sequence_id===originalEntry.collection_sequence_id){
              matchedEntry = testEntry;
            }              
          }
          if(matchedEntry===null){
            assert.fail(null, originalEntry.collection_sequence_id, "Expected to find the original entry on updated Workflow with specified collection_sequence_id.");
          }
          //if the original order was less than the order of inserted entry then the order on this entry should not have changed
          if(originalEntry.order < testPriority){
            assert.equal(matchedEntry.order, originalEntry.order, "Order should be unchanged for entry below inserted entry priority.");
          }
          else {
            assert.equal(matchedEntry.order, originalEntry.order + 1, "Order should have been incremented by 1 for every entry equal to or greater than inserted entry priortiy.");
          }
          
        }
        done();
      }));
      it('Should add entry with priority passed to workflow with existing entries, incrementing the priority of existing entries that are greater than or equal to the priority passed. Priority set to 0.', sinon.test(function(done){
        var testColSeqID = 3435353;
        var testPriority = 0;
        var test_workflow = JSON.parse(JSON.stringify(workflows[1].results[0]));
        var entriesLengthPreCall = test_workflow.additional.sequence_entries.length;
        
        var insertedEntry = workflowModel.insertCollectionSequenceIntoWorkflowEntries(test_workflow, testColSeqID,
          testPriority);
        
        //check that inserted entry has specified 
        assert(test_workflow.additional.sequence_entries.length === entriesLengthPreCall + 1, "Entries number should increase by 1" );
        assert(insertedEntry!==null && insertedEntry!==undefined, "Inserted entry should not be null or undefined");
        assert.equal(insertedEntry.collection_sequence_id, testColSeqID, "ID of inserted entry should be that of ID passed into method.");
        assert.equal(insertedEntry.order, testPriority, "Expecting order to have been set to the the priority passed.");
        assert.equal(insertedEntry.sequence_workflow_id, test_workflow.id, "'sequence_workflow_id' property should be set to the id of the workflow entry added to.");
        
        //check that entries already on the workflow were incremented
        var originalWorkflow = workflows[1].results[0];
        for(var originalEntry of originalWorkflow.additional.sequence_entries){
          var matchedEntry = null;
          for(var testEntry of test_workflow.additional.sequence_entries){
            if(testEntry.collection_sequence_id===originalEntry.collection_sequence_id){
              matchedEntry = testEntry;
            }              
          }
          if(matchedEntry===null){
            assert.fail(null, originalEntry.collection_sequence_id, "Expected to find the original entry on updated Workflow with specified collection_sequence_id.");
          }
          assert.equal(matchedEntry.order, originalEntry.order + 1, "Order should have been incremented by 1 for every entry.");
        }
        done();
      }));
    });
  });
});