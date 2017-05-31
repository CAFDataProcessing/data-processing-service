## CAF_12053 - Data Processing Service CreateWorkflow - negative tests ##

Verify that the CreateWorkflow service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateWorkflow call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateWorkflow call returns the expected error codes and the database is not updated with the new Workflow

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
