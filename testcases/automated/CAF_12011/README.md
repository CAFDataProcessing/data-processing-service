## CAF_12011 - Data Processing Service UpdateActionType - negative tests ##

Verify that the UpdateActionType service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an UpdateActionType call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The UpdateActionType call returns the expected error codes and the database is not updated and the Action Type remains unchanged

**JIRA Link** - [CAF-1390](https://jira.autonomy.com/browse/CAF-1390)
