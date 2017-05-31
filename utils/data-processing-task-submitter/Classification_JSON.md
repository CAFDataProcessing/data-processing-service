# Classification Workflow JSON Format for Task Submitter

## Summary

The task submitter supports creating a classification workflow for use in the processing workflow used on messages it sends. The workflow should be defined in JSON format in a file the submitter can access. The structure of this JSON is described in this document. The creation of the classification workflow is achieved through the classification [creation-util](https://github.hpe.com/caf/classification-service/tree/develop/creation-util) library.

## Format

The file is expected to define a single workflow, including all its processing rules, conditions and actions. Objects and their properties are supported as defined in the Classification API contract, [here](https://pages.github.hpe.com/caf/data-processing-service/pages/en-us/Classification/API), differing where noted below.

### Workflow

A top level node, the workflow node should define the properties of the workflow as supported by the API and also the classification rules of the workflow.

```
"workflow": {
  "name": "Classification Workflow",
  "description": "A workflow for classification.",
  "classificationRules": []
}
```

The classificationRules property on the workflow should contain all classification rules that are to be added under the workflow.

### Classification Rules

In the classificationRules property of the workflow, classification rules can be defined with the following information;

```
{
  "name": "Classification Rule",
  "description": "Compares document against a set of classifications.",
  "priority": 100,
  "ruleClassifications": [],
  "ruleConditions": []
}
```

The ruleClassifications property should be used to define all rule classifications under this classification rule and the ruleConditions property should define the conditions under which this classification rule is evaluated.

### Rule Classifications

In the ruleClassifications property of a classification rule, rule classifications can be defined by providing either the classificationName property or the classificationId property.

```
{
  "classificationName": "Receipts"
}
```

```
{
  "classificationId": 1
}
```

If the name is provided then it will be resolved to the ID of the matching classification before the rule classification is created. Classification ID will take precedence over the classification name.

### Rule Conditions

In the ruleConditions property of a classification rule, rule conditions can be defined as described in the API contract. Supported condition types are boolean, date, exists, not, number, regex, string and termlist (which may also be specified as lexicon).

e.g.
```
{
  "name": "My rule condition",
  "additional": {
    "type": "number",
    "field": "Keyview Type",
    "operator": "eq",
    "value": "345"
  }
}
```

If using a termlist condition, the name of the term list may be specified in the value property and it will be resolved to the appropriate ID.

### Classifications

A top level node, classifications can be defined as described in the API contract;

e.g.
{
  "name": "My classification",
  "description": "An example classification",
  "additional": {
    "type": "boolean",
    "operator": "OR",
    "children": [{
      "name": "Email Subject field contains Receipt wordings",
      "additional": {
        "type": "termlist",
        "field": "Title",
        "value": "Receipt Wording"
      }
    },
    {
      "name": "Email Body field contains Receipt wordings",
      "additional": {
        "type": "termlist",
        "field": "Document Content",
        "value": "Receipt Wording"
      }
    }]
  }
}

If using a termlist condition, the name of the term list may be specified in the value property and it will be resolved to the appropriate ID.

### Term Lists

A top level node, term lists can be defined as described in the API and also specifying the terms that the term list composes.

```
"termLists": [{
  "name": "Travel Company Email Domain Names",
  "description": "A lexicon of Travel Company Email Domain Names",
  "terms": []
}]
```

The terms property should contain the terms to add to this term list.

### Terms

In the terms property of a termlist, terms can be defined as described in the API contract.

e.g.
```
{
  "expression": "\"booking*@*.com\" OR \"reservation*@*.com\" OR \"travel*@*.com\"",
  "type": "text"
}
```

## Example

A full classification workflow example in this format can be seen [here](./example_files/classification_workflow_input.json).