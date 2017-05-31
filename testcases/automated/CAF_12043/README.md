## CAF_12043 - Data Processing Service CreateRule - negative tests ##

Verify that the CreateRule service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateRule call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateRule call returns the expected error codes and the database is not updated with the new Rule

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
