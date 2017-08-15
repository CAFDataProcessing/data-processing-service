!not-ready-for-release!

#### Version Number
${version-number}

#### New Features
- [CAF-3295](https://jira.autonomy.com/browse/CAF-3295): Add support to initialization library to remove existing processing workflows, classification workflows, boilerplate expressions and tags before creation based on their names.
  The initialization library classes for processing, classification and boilerplate all support the overwriteExisting property when creating entities through the classes. Default behaviour when the property is omitted is to remove existing entities based on their names matching the current names to be used in creation. The task submitter supports changing this behaviour through a new environment variable `CAF_TASKSUBMITTER_BASEDATA_OVERWRITE_EXISTING`.

#### Bug Fixes
- [CAF-3352](https://jira.autonomy.com/browse/CAF-3352): The negated condition on Not condition could not be edited.
  When action or rule conditions of type boolean are created the IDs of their children are returned and they can be updated, retrieved and deleted. Attempting to perform operations on the condition that was negated in a Not condition returned an error message. Fix has been applied to bring Not conditions in line with Boolean conditions allowing edits of the negated condition directly. This means that either the entire Not condition can be used in operations or just the negated condition (varying the ID appropriately).

#### Known Issues
