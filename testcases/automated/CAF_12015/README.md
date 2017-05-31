## CAF_12015 - Data Processing Service DeleteAction - negative tests ##

Verify that the DeleteAction service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an DeleteAction call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The DeleteAction call returns the expected error codes and the database is not updated as the Action should still be present

**JIRA Link** - [CAF-1290](https://jira.autonomy.com/browse/CAF-1290)
