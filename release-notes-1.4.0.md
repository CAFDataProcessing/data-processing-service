#### Version Number
${version-number}

#### New Features
- [CAF-3493](https://jira.autonomy.com/browse/CAF-3493): Family task submitter has been updated to pass the CONTENT field as a metadata reference.
  Family task submitter will now always pass the CONTENT field as a metadata reference, which can be used to verify that metadata references are correctly handled for a family of documents being processed in a workflow.

#### Bug Fixes
- [CAF-3352](https://jira.autonomy.com/browse/CAF-3352): Not condition children underneath Not conditions could not be edited.
  When a Not condition was created with `condition` set to another Not condition it was not possible to edit the next level of conditions being negated. This has been fixed to allow Rule and Action Condition API calls to retrieve conditions at all levels in the Not chain.
- [CAF-3350](https://jira.autonomy.com/browse/CAF-3350): Removal of 'fragment' and 'text' conditions from processing API contract.
  The condition types 'fragment' and 'text' have been removed from the swagger contract for the processing API. Logic behind this is that fragment conditions are too complex to expose to users and text conditions require the processing API to have access to a running Elasticsearch instance which the default Data Processing deployment does not enable for the processing API.
- [CAF-3328](https://jira.autonomy.com/browse/CAF-3328): Updating a processing rule cleared actions that were set on the rule.
  Fixed an issue in the update logic for a processing rule in the processing API where actions set under a rule were lost following an update request to the rule.
