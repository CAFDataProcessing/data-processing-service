## CAF_12045 - Data Processing Service DeleteRule - negative tests ##

Verify that the DeleteRule service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an DeleteRule call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The DeleteRule call returns the expected error codes and the database is not updated as the Rule should still be present

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
