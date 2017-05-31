## CAF_12025 - Data Processing Service CreateRuleCondition - negative tests ##

Verify that the CreateRuleCondition service call returns correct error codes when invalid information is provided

**Test Steps**

Using the Data Processing service perform an CreateRuleCondition call, entering invalid information and then check the Policy Workflow database

**Test Data**

N/A

**Expected Result**

The CreateRuleCondition call returns the expected error codes and the database is not updated with the new RuleCondition

**JIRA Link** - [CAF-1292](https://jira.autonomy.com/browse/CAF-1292)
