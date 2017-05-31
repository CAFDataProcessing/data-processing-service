## CAF_12003 - Data Processing Service CreateActionType - negative tests ##

Verify that the CreateActionType service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateActionType call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateActionType call returns the expected error codes and the database is not updated with the new Action Type

**JIRA Link** - [CAF-1390](https://jira.autonomy.com/browse/CAF-1390)
