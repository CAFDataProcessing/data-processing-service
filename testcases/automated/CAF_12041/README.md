## CAF_12041 - Data Processing Service UpdateRuleCondition - negative tests ##

Verify that the UpdateRuleCondition service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an UpdateRuleCondition call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The UpdateRuleCondition call returns the expected error codes and the database is not updated and the RuleCondition remains unchanged

**JIRA Link** - [CAF-1292](https://jira.autonomy.com/browse/CAF-1292)
