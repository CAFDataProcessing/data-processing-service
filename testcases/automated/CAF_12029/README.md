## CAF_12029 - Data Processing Service DeleteRuleCondition - negative tests ##

Verify that the DeleteRuleCondition service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an DeleteRuleCondition call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The DeleteRuleCondition call returns the expected error codes and the database is not updated as the RuleCondition should still be present

**JIRA Link** - [CAF-1292](https://jira.autonomy.com/browse/CAF-1292)
