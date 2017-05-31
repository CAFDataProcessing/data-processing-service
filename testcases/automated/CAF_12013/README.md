## CAF_12013 - Data Processing Service CreateAction - negative tests ##

Verify that the CreateAction service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateAction call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateAction call returns the expected error codes and the database is not updated with the new Action

**JIRA Link** - [CAF-1290](https://jira.autonomy.com/browse/CAF-1290)
