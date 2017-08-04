!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
- [CAF-3295](https://jira.autonomy.com/browse/CAF-3295): Add support to initialization library to remove existing processing workflows, classification workflows, boilerplate expressions and tags before creation based on their names.
  The initialization library classes for processing, classification and boilerplate all support the overwriteExisting property when creating entities through the classes. Default behaviour when the property is omitted is to remove existing entities based on their names matching the current names to be used in creation. The task submitter supports changing this behaviour through a new environment variable `CAF_TASKSUBMITTER_BASEDATA_OVERWRITE_EXISTING`.

#### Known Issues
