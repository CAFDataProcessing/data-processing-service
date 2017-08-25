#### Version Number
${version-number}

#### New Features

#### Bug Fixes
- [CAF-3350](https://jira.autonomy.com/browse/CAF-3350): Removal of 'fragment' and 'text' conditions from processing API contract.
  The condition types 'fragment' and 'text' have been removed from the swagger contract for the processing API. Logic behind this is that fragment conditions are too complex to expose to users and text conditions require the processing API to have access to a running Elasticsearch instance which the default Data Processing deployment does not enable for the processing API.

#### Known Issues
