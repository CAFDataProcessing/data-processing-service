## CAF_12021 - Data Processing Service UpdateAction - negative tests ##

Verify that the UpdateAction service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an UpdateAction call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The UpdateAction call returns the expected error codes and the database is not updated and the Action remains unchanged

**JIRA Link** - [CAF-1290](https://jira.autonomy.com/browse/CAF-1290)
