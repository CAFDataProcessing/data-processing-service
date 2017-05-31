## CAF_12001 - Data Processing Service - Health Check ##

Verify that the Data Processing service Health Check works as expected

**Test Steps**

1. Set up system and deploy the data processing service container 
2. Call the web service health check using the url: http://(hostInfo):(hostPort)/data-processing-service/v1/healthCheck

**Test Data**

N/A

**Expected Result**

The service will return a status of 200 and a boolean value of "true"

**JIRA Link** - [CAF-1407](https://jira.autonomy.com/browse/CAF-1407)
