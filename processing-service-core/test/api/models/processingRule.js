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
//unit tests for processingRule.js
var assert = require("../helpers/assertHelper.js");
var sinon = require("sinon");
var Q = require('q');
var processingRule = require("../../../api/models/processingRule.js");
var processingRuleStubs = require("../helpers/processingRuleStubHelper.js");

//will be setting stubs on these
var workflowModel = require('../../../api/models/policy_api/workflow.js');
var colSeqModel = require('../../../api/models/policy_api/collectionSequence.js');

//get test data
var processingData = require("../data/processing.js");
var workflows = processingData.workflows;
var workflowEntries = processingData.workflowEntries;
var colSeqs = processingData.collectionSequences;
var expectedRules = processingData.rules;

//resolves timeout issue when using sinon.test
sinon.config = {
  useFakeTimers: false
};

describe('models - processingRule', function(){
  describe('getRule', function(){
    describe('Basic get rule test', function(){
      it('Should get a rule with specified ID', sinon.test(function(){
        var test_project_id = 'Default';
        var test_ruleId = 1;
        
        var getRuleParams = {
          id: test_ruleId,
          project_id: test_project_id
        };
        
        var errorCallback = function(error){
          throw new Error("Error Callback was called by updateRule: "+error);
        };
        //set up stub for retrieving collection sequence by ID
        var getColSeqStub = this.stub(colSeqModel, 'get', 
          processingRuleStubs.getColSeqStubFunction(test_project_id, test_ruleId)
        );
        var expectedColSeq = JSON.parse(JSON.stringify(colSeqs[test_ruleId].results[0]));
        var expectedRule = {
          name: expectedColSeq.name,
          description: expectedColSeq.description,
          id: expectedColSeq.id
        };
        
        var responseToWriteTo = {statusCode: 200};
        responseToWriteTo.status = function(statusValue){
          responseToWriteTo.statusCode = statusValue;
        };
        responseToWriteTo.json = function(data){
          assert(data !== undefined && data !== null, "Data passed to response json should not be null or undefined.");
          
          assert.equal(data.message, undefined, "'message' property is present on error so should not be on response: ");
          
          assert(responseToWriteTo.json.calledOnce, "Expecting json method on response to have been called once.");
          assert.equal(responseToWriteTo.statusCode, 200, "Expecting status code to be 200.");
          
          //check that expected rule returned
          assert.equal(data.name, expectedRule.name, "Name on returned rule should be as expected.");
          assert.equal(data.description, expectedRule.description, "Description on returned rule should be as expected.");
          assert.equal(data.id, expectedRule.id, "ID on returned rule should be as expected.");
          
          done();
        };
        
        this.spy(responseToWriteTo, "json");        
        
        processingRule.getRule(getRuleParams, responseToWriteTo, errorCallback);
      }));
    });
  });  
  describe('deleteRule', function(){
    describe('Basic delete rule test', function(){
      it('Should delete a rule', sinon.test(function(done){
        var test_project_id = 'Default';
        var ruleToDeleteId = 3;
        //this is the id of the workflow that the rule is part of in the test data.
        var test_workflowId = 1;
        var deleteRuleParams = {
          id: ruleToDeleteId,
          project_id: test_project_id
        };
        var errorCallback = function(error){
          throw new Error("Error Callback was called by updateRule: "+error);
        };
        //set up getWorkflowBy Col Seq stub for retrieving all workflows that sequence is part of
        var getWorkflowByColSeqStub = this.stub(workflowModel, "getWorkflowsWithCollectionSequenceId",
          processingRuleStubs.getWorkflowsWithCollectionSequenceIdStubFunction(test_project_id, ruleToDeleteId)
        );
        //set up stub for retrieving workflow entry details
        var getWorkflowByIdStub = this.stub(workflowModel, "getWorkflowById",
          processingRuleStubs.getWorkflowByIDStubFunction(test_project_id, test_workflowId)
        );
        var expectedWorkflow = JSON.parse(JSON.stringify(workflows[1].results[0]));
        //remove the entry with this collection sequence ID
        var entriesIndex = expectedWorkflow.additional.sequence_entries.length;
        while(entriesIndex--){
          if(expectedWorkflow.additional.sequence_entries[entriesIndex].collection_sequence_id === ruleToDeleteId){
            expectedWorkflow.additional.sequence_entries.splice(entriesIndex, 1);
            break;
          }
        }        
        //set up stub for when update workflow is called removing the collection sequence from the workflow
        var updateWorkflowStub = this.stub(workflowModel, "updateWorkflow",
          processingRuleStubs.updateWorkflowStubFunction(test_project_id, expectedWorkflow)
        );
        
        //set up delete colSeq. stub
        var deleteColSeqStub = this.stub(colSeqModel, "delete",
          processingRuleStubs.deleteColSeqStubFunction(test_project_id, ruleToDeleteId)
        );
        
        var responseToWriteTo = {statusCode: 200};
        responseToWriteTo.status = function(statusValue){
          responseToWriteTo.statusCode = statusValue;
        };
        responseToWriteTo.json = function(data){
          assert(data !== undefined && data !== null, "Data passed to response json should not be null or undefined.");
          
          assert.equal(data.message, undefined, "'message' property is present on error so should not be on response: ");
          
          assert(responseToWriteTo.json.calledOnce, "Expecting json method on response to have been called once.");
          assert(getWorkflowByColSeqStub.calledOnce, "Expecting get workflow by Col Seq method to have been called once.");
          assert(getWorkflowByIdStub.calledOnce, "Expecting get workflow by ID method to have been called once.");
          assert(updateWorkflowStub.calledOnce, "Expecting update workflow method to have been called once.");
          assert(deleteColSeqStub.calledOnce, "Expecting delete col seq. method to have been called once.");
          assert.equal(responseToWriteTo.statusCode, 200, "Expecting status code to be 200.");
          
          done();
        };
        
        this.spy(responseToWriteTo, "json");        
        
        processingRule.deleteRule(deleteRuleParams, responseToWriteTo, errorCallback);
      }));
    });
  });  
  describe('updateRule', function(){
    describe('Basic update test', function(){
      it('Should update a rule', sinon.test(function(done){
        var test_project_id = 'Default';
        var test_rule = {
          id: 1,
          name: 'test_rule name',
          description: 'test_rule_decription'
        };
        var updateRuleParams = {
          id: test_rule.id,
          name: test_rule.name,
          description: test_rule.description,
          project_id: test_project_id
        };
        var errorCallback = function(error){
          throw new Error("Error Callback was called by updateRule: "+error);
        };
        //set up update stub
        var updateColSeqStub = this.stub(colSeqModel, "update",
          processingRuleStubs.updateColSeqStubFunction(test_project_id, test_rule)
        );
        
        var responseToWriteTo = {statusCode: 200};
        responseToWriteTo.status = function(statusValue){
          responseToWriteTo.statusCode = statusValue;
        };
        responseToWriteTo.json = function(data){
          assert(data !== undefined && data !== null, "Data passed to response json should not be null or undefined.");
          
          assert.equal(data.message, undefined, "'message' property is present on error so should not be on response: ");
          
          assert(responseToWriteTo.json.calledOnce, "Expecting json method on response to have been called once.");
          assert(updateColSeqStub.calledOnce, "Expecting update col seq. method to have been called once.");
          assert.equal(responseToWriteTo.statusCode, 200, "Expecting status code to be 200.");
          
          //check that passed data is in the form of expected rule
          assert.equal(data.name, test_rule.name, "Name on updated rule should be as expected.");
          assert.equal(data.description, test_rule.description, "Description on updated rule should be as expected.");
          assert.equal(data.id, test_rule.id, "ID on updated rule should be as expected.");
          
          done();
        };
        
        this.spy(responseToWriteTo, "json");        
        
        processingRule.updateRule(updateRuleParams, responseToWriteTo, errorCallback);
      }));
    });
  });  
  describe('createRule', function(){
    describe('Basic create test.', function(){
      it('Should call create collection sequence and updateWorkflow to save a new Rule.', sinon.test(function(done){
        var test_workflowId = 1;
        var test_project_id = 'Default';
        var test_rule = {
          name: 'test_rule name',
          description: 'test_rule_decription'
        };
        var createRuleParams = {
          name: test_rule.name,
          description: test_rule.description,
          project_id: test_project_id,
          workflowId: test_workflowId
        };
        var errorCallback = function(error){
          throw new Error("Error Callback was called by createRule: "+error);
        };
        //create stubs for the calls we expect
        var getWorkflowByIdStub = this.stub(workflowModel, "getWorkflowById", 
          processingRuleStubs.getWorkflowByIDStubFunction(test_project_id, test_workflowId)
        );
        //set id counter to one more than the id of entries on the workflow
        var createdSequencesCounter = 6;
        var createColSeqStub = this.stub(colSeqModel, "create",
          processingRuleStubs.createColSeqStubFunction(test_project_id, test_rule, createdSequencesCounter)
        );
        var expectedColSeq = {
          description: test_rule.description,
          id: 7,
          name: test_rule.name          
        };
        var test_expectedWorkflow = JSON.parse(JSON.stringify(workflows[test_workflowId].results[0]));
        //expecting an additional sequence entry added for the new Col Seq.
        var insertedEntry = workflowModel.insertCollectionSequenceIntoWorkflowEntries(test_expectedWorkflow, expectedColSeq.id);
                
        var updateWorkflowStub = this.stub(workflowModel, "updateWorkflow", 
          processingRuleStubs.updateWorkflowStubFunction(test_project_id, test_expectedWorkflow)
        );
        
        var responseToWriteTo = {statusCode: 200};
        responseToWriteTo.status = function(statusValue){
          responseToWriteTo.statusCode = statusValue;
        };
        responseToWriteTo.json = function(data){
          assert(data !== undefined && data !== null, "Data passed to response json should not be null or undefined.");
          
          assert.equal(data.message, undefined, "'message' property is present on error so should not be on response: ");
          
          assert(responseToWriteTo.json.calledOnce, "Expecting json method on response to have been called once.");
          assert(createColSeqStub.calledOnce, "Expecting create col seq. method to have been called once.");
          assert(getWorkflowByIdStub.calledOnce, "Expecting get workflow ID method to have been called once.");
          assert(updateWorkflowStub.calledOnce, "Expecting update workflow method to have been called once.");
          assert.equal(responseToWriteTo.statusCode, 200, "Expecting status code to be 200.");
          
          //check that passed data is in the form of expected rule
          assert.equal(data.name, expectedColSeq.name, "Name on created rule should be as expected.");
          assert.equal(data.description, expectedColSeq.description, "Description on created rule should be as expected.");
          assert.equal(data.id, expectedColSeq.id, "ID on created rule should be as expected.");
          assert.equal(data.priority, insertedEntry.order, "Priority on created rule should be as expected.");
          
          done();
        };
        this.spy(responseToWriteTo, "json");        
        
        processingRule.createRule(createRuleParams, responseToWriteTo, errorCallback);
      }));
    });
  });  
  describe('getRules()', function(){
    describe('default pageNum and pageSize', function(){
      it('Should return a default of first 100 results when no pageNum or pageSize set.', sinon.test(function(done){
        var test_workflowId = 1;
        var test_project_id = 'Default';
        
        var getRulesParams = {
          workflowId: test_workflowId,
          project_id: test_project_id
        };
        var errorCallback = function(error){
          throw new Error("Error Callback was called by getRules: "+error);
        };
        
        //create stubs for the calls we expect
        var workflowEntriesStub = this.stub(workflowModel, "getWorkflowEntriesByWorkflowId",
          processingRuleStubs.getWorkflowEntriesStubFunction(test_project_id, test_workflowId, undefined, undefined)
        );
        
        //there should only ever be one request to get each ID so keep an array of already requested IDs
        var alreadyRequestedIds = [];
        this.stub(colSeqModel, "get", 
          processingRuleStubs.getColSeqsStubFunction(test_project_id, alreadyRequestedIds)
        );
                
        //mocking up a response object
        var responseToWriteTo = {statusCode: 200};
        responseToWriteTo.status = function(statusValue){
          responseToWriteTo.statusCode = statusValue;
        };
        responseToWriteTo.json = function(data){
          assert(data !== undefined && data !== null, "Data passed to response json should not be null or undefined.");
          
          assert.equal(data.message, undefined, "'message' property is present on error so should not be on response.");
          
          assert(responseToWriteTo.json.calledOnce, "Expecting json method on response to have been called once.");
          assert(workflowEntriesStub.calledOnce, "Expecting get workflow entries method to have been called once.");
          assert.equal(responseToWriteTo.statusCode, 200, "Expecting status code to be 200.");          
          
          assert.equal(data.totalHits, expectedRules.totalHits, "Expecting all collection sequences defined in test data to be returned.");
          assert(data.rules !== undefined && data.rules !== null, "Expecting 'rules' property on result to be defined.");
          assert.equal(data.rules.length, expectedRules.rules.length, "Expecting correct number of returned rules.");
          
          
          for(var expectedRule of expectedRules.rules){
            //find matching rule
            var returnedRule = data.rules.find(
              function(ruleToCheck){
                if(ruleToCheck.id===null || ruleToCheck.id===undefined){
                  throw new Error("Rule returned had no 'id' property.");
                }
                return ruleToCheck.id===expectedRule.id;
              });
            assert(returnedRule!==null && returnedRule!==undefined, "Expecting a rule returned with ID: "+expectedRule.id);
            assert.equal(returnedRule.name, expectedRule.name, "Returned rule should have expected name.");
            assert.equal(returnedRule.description, expectedRule.description, "Returned rule should have expected description.");
            assert.equal(returnedRule.priority, expectedRule.priority, "Returned rule should have expected priority.");
          }
          done();
        };
        
        this.spy(responseToWriteTo, "json");        
        
        processingRule.getRules(getRulesParams, responseToWriteTo, errorCallback);
      }));
    });
  });  
});