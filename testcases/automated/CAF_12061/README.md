## CAF_12061 - Data Processing Service UpdateWorkflow - negative tests ##

Verify that the UpdateWorkflow service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an UpdateWorkflow call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The UpdateWorkflow call returns the expected error codes and the database is not updated and the Workflow remains unchanged

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
