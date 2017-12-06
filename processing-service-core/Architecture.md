# data processing service

## Detailed Overview

This implementation makes use of concepts in the Policy API to represent the requirements of the data-processing-service-contract. The Policy API is used as it is supported in the Policy Worker project to control the processing of items that the worker receives. This processing can then, based on the objects created in the Policy API, directly perform processing on the item or direct an item to other CAF Workers to provide support for Binary Hashing, Boilerplate text detection and more. This existing integration makes it desirable to use the Policy API as a back-end, with this project intending to simplify the interactions required with the Policy API to create and manage the Workflow for processing that a Worker will eventually use.

The policy API exposes a large number of configuration options. This simplified API aims to only expose those required for the scenario of data processing thus allowing a reduction in what the user has to provide.

A single object type in the contract may be represented in the Core Policy back-end as multiple types of different objects. The data-processing-service is responsible for managing those relationships and seamlessly representing them as the client facing types to the caller.

### Example

The data-processing-service contract ([here](./api/swagger/swagger.yaml)) defines an existing Action on a Rule as having the following properties;

#### Action

| Name          | Description  |
| ------------- | -----|
| id     | The identifier for the Action. |
| name     | The name of the Action. |
| description     | The description of the Action. |
| order     |  The order that an Action will be processed in relative to other Actions on the associated Rule. |
| settings     |  Used to control the behaviour of the Action. Specify the settings supported by a type here. |
| typeId     |  The identifier for the Action Type. |
| typeInternalName  | A string identifying the Action Type to use when processing with this Action. |

In the Core Policy API we can represent a single Action as: a Collection with a Policy that has an entry on a Collection Sequence. The split of properties across these types is as below;

#### Collection Sequence Entry

| Action Property | Policy API Property  | Notes |
| ------------- | ----- | ---- |
| order | order | The order on an Action maps to the order property of a Collection Entry on a Collection Sequence. |

#### Collection

| Action Property | Policy API Property  | Notes |
| ------------- | ----- | ---- |
| id | id | The ID of an Action maps to the ID of a Collection stored in the Policy back-end. |
| name | name | The name of an Action maps to the name of the Collection identified by the ID. |
| description | description | The description of an Action maps to the name of the Collection identified by the ID. |

#### Policy

| Action Property | Policy API Property  | Notes |
| ------------- | ----- | ---- |
| settings | details | The settings of an Action map to the details on a Policy stored in the Policy back-end. |
| typeId | name | Maps to the policy_type_id on a Policy. |

#### Policy Type

| Action Property | Policy API Property  | Notes |
| ------------- | ----- | ---- |
| typeInternalName | details | The typeInternalName of an Action maps to the short_name property of a Policy Type. |


Any request for an Action by its specified ID then involves the data-processing-service acting as an intermediary to retrieve the different components making up the object and building up the Action for return to the user.

![Representation of the communication between caller, data-processing-service and Policy API for a GetAction request](./GetAction.png)

On GetAction, the data-processing-service API will use the RuleID and ID of the Action passed in to the request to;
- Retrieve the 'order' for the Action by querying the Policy API for the Collection Entry on a Collection Sequence with ID matching the specified RuleID. The Entries refer to collection IDs which will be compared against the passed in Action ID.
- Retrieve a Collection from the Policy API using the passed in ID. The 'name' and 'description' will be pulled from this. This request will also return the ID of the Policy associated with the Collection.
- Retrieve a Policy from the Policy API using the Policy ID on the Collection. Pulling the 'settings' and 'typeId' from this response.
- Retrieve a Policy Type from the Policy API using the 'typeId'. Pulling the 'typeInternalName' value for this type.

Having retrieved this information the API then return an object with all the information pulled from these requests in the form of an Action.

### Types

#### Workflow
The top-level concept, a Workflow holds a set of Rules. It is the overall container of the processing logic and its ID can be submitted to the Policy Worker to process a document against which causes all Rules within the Workflow to be evaluated against the document. It maps to the concept of Workflow in the Policy API.

#### Rule
A Rule exists under a Workflow and contains a set of Actions to perform on a document. Each Rule executes in order as part of their Workflow. Rules can be enabled and disabled. This allows already established Rules to be part of a Workflow but temporarily not executed against any documents flowing through the system. Rules can have Conditions set on them set criteria for the documents they will execute their Actions against. A Rule maps to the combination of a Collection Sequence and a Workflow Entry in the Policy API.

On creation of a Rule a Policy Condition is created that is associated with the Rule, its name and notes field being in the format 'RULE_ID:$ID' e.g. RULE_ID:217 and a type of Boolean with no child conditions by default. This is the root rule condition and is used to hold Rule Conditions and referenced by Actions under a Rule. It is not exposed to the caller.

#### Action
An Action exists under a Rule and represents a logical processing operation to perform on a Document such as Metadata Extraction, Audio to Text translation etc. They refer to a particular Action Type and specify  settings for that type that should be used in execution e.g. An Action of type Text Extract can specify extractHash as 'true' to have a hash extracted for a document. They have a defined execution order. Actions may also have their own Conditions, e.g. an OCR Action may have a condition to only execute on image files. An Action must meet both the Rule level Conditions and its Actions level Conditions to be executed. An Action maps to the combination of a Collection and a Policy in the Policy API, with the internal name of the Policy pulled from the Policy Type set.

When an Action is created a Policy Condition is also created to hold Action Conditions for this Action (identifiable by name and notes field set to ACTION_ROOT). This is set as the Condition of the Collection representing the Action. This is a Boolean type Condition that by default will have a fragment type Condition pointing to the root Rule Condition applicable for the Action. By default no Rule Conditions will have been added, which will cause Actions to always match documents once created. The Action root Condition and fragment Condition pointing to the root Rule Condition are not exposed to the caller.

#### Action Type
An Action type represents a possible type of processing to perform against a document e.g. Text Extract, OCR, Speech to text. Types themselves are then used as part of Actions. Each type defines the settings that an Action may use in execution. They map to Policy Types in the Policy API.

#### Action Conditions

An Action Condition allows some criteria to be set on the execution of a specific Action against a document. They are individual to each Action and are effectively direct mappings to Policy Conditions (for convenience of the caller they allow omitting the 'type: "condition"' in their object definition). The swagger contract describes the models for the various condition types.

Action Conditions are added to the their Action's Action root Condition as children.

#### Rule Conditions

A Rule Condition allows some criteria to be set that must be met for the execution of all Actions underneath a Rule. As with Action Conditions they are effectively direct mappings to Policy conditions while allowing the omission of "'type: condition" in their definition. The swagger contract defines the various Condition models.

Rule Conditions are added as children of the root Rule Condition. Each Action has a fragment type Condition that points to this root Rule Condition so when the new Conditions are added they affect all the Actions under the Rule.