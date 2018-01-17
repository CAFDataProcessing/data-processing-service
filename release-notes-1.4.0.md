#### Version Number
${version-number}

#### New Features
- [CAF-3493](https://jira.autonomy.com/browse/CAF-3493): Family task submitter has been updated to pass the CONTENT field as a metadata reference.
  Family task submitter will now always pass the CONTENT field as a metadata reference, which can be used to verify that metadata references are correctly handled for a family of documents being processed in a workflow.
- [CAF-3878](https://jira.autonomy.com/browse/CAF-3878): Family task submitter now submits document worker tasks.
  Family task submitter has been changed to submit composite document worker task messages rather than policy worker messages so that it may be used with the new document worker based workflow worker implementation for chained workers. RabbitMQ environment variables names used by family submitter have also been changed to be in line with other CAF services. The previous property names are still supported.
- [CAF-3532](https://jira.autonomy.com/browse/CAF-3532): Task submitter now submits document worker tasks.
  Task submitter has been changed to submit composite document worker task messages rather than policy worker messages so that it may be used with the new document worker based workflow worker implementation for chained workers.

#### Bug Fixes
- [CAF-3352](https://jira.autonomy.com/browse/CAF-3352): Not condition children underneath Not conditions could not be edited.
  When a Not condition was created with `condition` set to another Not condition it was not possible to edit the next level of conditions being negated. This has been fixed to allow Rule and Action Condition API calls to retrieve conditions at all levels in the Not chain.
- [CAF-3350](https://jira.autonomy.com/browse/CAF-3350): Removal of 'fragment' and 'text' conditions from processing API contract.
  The condition types 'fragment' and 'text' have been removed from the swagger contract for the processing API. Logic behind this is that fragment conditions are too complex to expose to users and text conditions require the processing API to have access to a running Elasticsearch instance which the default Data Processing deployment does not enable for the processing API.
- [CAF-3328](https://jira.autonomy.com/browse/CAF-3328): Updating a processing rule cleared actions that were set on the rule.
  Fixed an issue in the update logic for a processing rule in the processing API where actions set under a rule were lost following an update request to the rule.

#### Miscellaneous
- The data-processing-initialization project has been moved into its own repository which can be found [here](https://github.com/CAFDataProcessing/data-processing-initialization).
- Classification Service has been deprecated and all references to it have been removed from this project. This includes the database container no longer has the Classification database pre-installed along with removal of the Classification Service and Classification Worker.
