#### Version Number
${version-number}

#### New Features
- [SCMOD-3477](https://jira.autonomy.com/browse/SCMOD-3477): A new feature to allow the management of global configs.
- [SCMOD-3111](https://jira.autonomy.com/browse/SCMOD-3111): Tenant configuration store  
    The Data Processing Service has been enhanced with the facility to store tenant specific configuration.  After a tenant setting has been registered with the service values can then be stored on a per-tenant basis.
- [SCMOD-3282](https://jira.autonomy.com/browse/SCMOD-3282): Added Nodejs6 compatability.
     Changed the base image for data processing service container from  [Nodejs4](https://github.com/CAFapi/opensuse-nodejs4-image) to [Nodejs6](https://github.com/CAFapi/opensuse-nodejs6-image). 

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
