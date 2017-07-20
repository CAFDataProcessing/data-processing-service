
#### Version Number
${version-number}

#### New Features
- [CAF-3056](https://jira.autonomy.com/browse/CAF-3056): Create a Family Task Submitter  
The "Data-Processing-Family-Task-Submitter" has been added which queries Elasticsearch using a family reference for a document that has already been indexed, reconstitutes the document and its attachments to construct a policy message, and sends this to a specified policy worker queue.

- [CAF-3156](https://jira.autonomy.com/browse/CAF-3156): Data Processing Family Classification  
Uses the latest classification-service libraries which add the classificationTarget property to classifications, allowing control of the scope of classification evaluation e.g. root document only, or root document and children that were passed.

- [CAF-3198](https://jira.autonomy.com/browse/CAF-3198): Extract Workflow Initialization Logic to a separate project  
The data-processing-initialization library has been added which is a utility project with logic to initialize base data for the Processing and Classification workflows and the Boilerplate expressions and tags.

- [CAF-3290](https://jira.autonomy.com/browse/CAF-3290): Add ability to extend or override default workflow  
Extended the workflow initialization code with the ability to use an overlay type of file or structure which can then be merged into the base definition. This allows use of the default workflow but with replacements/extensions injected during provisioning, thus simplifying upgrades.

#### Known Issues
