# Boilerplate JSON Format for Task Submitter

## Summary

The task submitter supports creating boilerplate expressions and tags for use in the processing workflow used on messages it sends. The expressions and tags should be defined in JSON format in a file the submitter can access. The structure of this JSON is described in this document. The creation of the expressions and tags is achieved through the [util-boilerplate-creation](https://github.hpe.com/caf/boilerplate-service/tree/develop/util-boilerplate-creation) library.

## Format

### Project ID

The project ID to create the expressions and tags under should be provided as a top level node.

```
{
  ...
  "projectId": "Default",
  ...
```

This should match the project ID that messages are to be submitted with.

### Expressions

The expressions to create should be provided as a top level node. Each expression should define properties as shown below;

```
{
"expressions": [{
    "tempId": 1,
    "name": "HP old",
    "description": "HP old",
    "expression": "HP Ltd"
  }]
}
```

The tempId property allows the expression to be used in tags defined in this file. During creation the correct ID will be used once the expression is created.

### Tags

The tags to create should be provided as a top level node. Each tag should define properties as shown below;

```
{
  "tags": [{
    "name": "Disclaimers",
    "description": "Disclaimers Tag",
    "defaultReplacementText": "<redacted disclaimer>",
    "boilerplateExpressions": [2,3,4,9]
  }]
}
```

The boilerplateExpressions property on the tag should use the tempId values of expressions in the file.

## Example

A full example in this format can be seen [here](./example_files/boilerplate_input.json).