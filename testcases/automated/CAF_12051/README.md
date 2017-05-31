## CAF_12051 - Data Processing Service UpdateRule - negative tests ##

Verify that the UpdateRule service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an UpdateRule call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The UpdateRule call returns the expected error codes and the database is not updated and the Rule remains unchanged

**JIRA Link** - [CAF-1413](https://jira.autonomy.com/browse/CAF-1413)
