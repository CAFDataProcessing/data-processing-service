## CAF_12005 - Data Processing Service DeleteActionType - negative tests ##

Verify that the DeleteActionType service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an DeleteActionType call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The DeleteActionType call returns the expected results and the database is updated by deleting the Action Type

**JIRA Link** - [CAF-1390](https://jira.autonomy.com/browse/CAF-1390)
