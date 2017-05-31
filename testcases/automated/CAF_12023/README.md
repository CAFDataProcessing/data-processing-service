## CAF_12023 - Data Processing Service CreateActionCondition - negative tests ##

Verify that the CreateActionCondition service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateActionCondition call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateActionCondition call returns the expected error codes and the database is not updated with the new ActionCondition

**JIRA Link** - [CAF-1292](https://jira.autonomy.com/browse/CAF-1292)
