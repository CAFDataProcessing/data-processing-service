## CAF_12055 - Data Processing Service DeleteWorkflow - negative tests ##

Verify that the DeleteWorkflow service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an DeleteWorkflow call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The DeleteWorkflow call returns the expected error codes and the database is not updated as the Workflow should still be present

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
