# data-processing-task-submitter

## Summary

This is an application that submits task messages for files in a folder to a queue, for consumption by a workflow worker. The ID of an existing workflow can be provided for use on the messages or a definition of a workflow can be provided and it will be created for use in the messages.

## Configuration

The application can be configured through any one of the followed ways;

* files on the classpath for each type of configuration
* files in a folder specified by the environment variable 'CAF_CONFIG_LOCATION'
* specifying configuration properties as environment variables

### RabbitMQ Properties

These properties control communication with RabbitMQ. They may be specified in a file 'rabbit.properties' or as environment variables.

#### CAF_RABBITMQ_HOST

Hostname where rabbitMQ is running.

#### CAF_RABBITMQ_PORT

Port to communicate with rabbitMQ to publish/receive messages.

#### CAF_RABBITMQ_USERNAME

Username to use for rabbitMQ application.

#### CAF_RABBITMQ_PASSWORD

Password to use for rabbitMQ application.

#### CAF_RABBITMQ_MAX_ATTEMPTS

Number of times to try and establish connection to rabbitMQ if unable to connect. Default to '-1' which indicates infinite retry attempts will be made.

#### CAF_RABBITMQ_BACKOFF_INTERVAL

The time (in seconds) between re-attempts if communication with RabbitMQ fails for any reason. Increases with subsequent failures. Defaults to 5.

#### CAF_RABBITMQ_MAX_BACKOFF_INTERVAL

The maximum time (in seconds) between re-attempts if communication with RabbitMQ fails for any reason. Defaults to 15.

#### CAF_RABBITMQ_PUBLISH_QUEUE

The queue the application should send messages to. This must be set.

### Storage Properties

This property may be specified in a file 'storage.properties' or as an environment variable.

#### CAF_STORAGE_DATA_DIRECTORY

The directory containing stored data referenced by task messages. Should be the same data store as used by the workflow worker so it may retrieve data for submitted message.

### Task Submitter Properties

These are properties specific to the functionality of the task submitter application. They may be specified in a file 'taskSubmitter.properties' or as environment variables.

#### CAF_TASKSUBMITTER_DOCUMENT_INPUT_DIRECTORY

Directory to monitor for files to submit as messages. Required.

#### CAF_TASKSUBMITTER_PROJECTID

ProjectId to use on submitted messages. Required.

#### CAF_TASKSUBMITTER_SENT_DOCUMENTS_DIRECTORY

Directory to move a file to once it has had its associated task message sent. This is optional, if not supplied then files will not be moved from the input directory.

#### CAF_TASKSUBMITTER_WORKFLOW_ID

Data Processing workflow ID to send on submitted messages. If this is not set then it is assumed that a workflow is to be created and used on sent messages, which requires that the appropriate base data properties are set.

The base data properties provide the information required so that the processing workflow to use can be created. If the workflow contains references to boilerplate expressions/tags or classification workflows they can also be created by specifying the required details to create those items.

#### CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_INPUT_FILE

File path to a JSON definition of the boilerplate expressions and tags that should be created for use in the processing workflow that will be created. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE' is true.

The format of the JSON expected is described [here](./Boilerplate_JSON.md).

#### CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_OUTPUT_FILE

File path to write details about created boilerplate expressions and tags to. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE' is true.

#### CAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_API_URL

URL to a boilerplate API web service. This will be used to create expressions and tags. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE' is true.

#### CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_API_URL

URL to a classification API web service. This will be used to create the classification workflow. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION' is true.

#### CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_INPUT_FILE

File path to a JSON definition of the classification workflow that should be created for use in the processing workflow that will be created. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION' is true.

The format of the JSON expected is described [here](./Classification_JSON.md).

#### CAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_OUTPUT_FILE

File path to write details about created classification workflow to. Required if 'CAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION' is true.

#### CAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE

Indicates whether some boilerplate base data needs to be created. Defaults to false.

#### CAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION

Indicates whether some classification base data needs to be created. Defaults to false.

#### CAF_TASKSUBMITTER_BASEDATA_EXTERNAL_API_READY_RETRY_ATTEMPTS

Number of times the application will try to contact an API required for base data creation before exiting during startup environment check. Defaults to -1, meaning the application will continue attempting to contact an API until it responds as healthy.

#### CAF_TASKSUBMITTER_BASEDATA_PROCESSING_API_URL

URL to a processing API web service. This will be used to create the processing workflow. Required if no workflow ID is provided.

#### CAF_TASKSUBMITTER_BASEDATA_WORKFLOW_INPUT_FILE

File path to a JSON definition of the processing workflow that should be created. Required if no workflow ID is provided.

The format of the JSON expected is described [here](./Workflow_JSON.md).

## Running the Application

The task submitter produces a single jar file that contains all its class dependencies. Examples of running the application are shown below.

### Existing Workflow

```
java -DCAF_TASKSUBMITTER_PROJECTID=Default \
  -DCAF_TASKSUBMITTER_DOCUMENT_INPUT_DIRECTORY=/input \
  -DCAF_TASKSUBMITTER_WORKFLOW_ID=1 \
  -DCAF_RABBITMQ_PUBLISH_QUEUE=input-queue \
  -DCAF_RABBITMQ_USERNAME=guest \
  -DCAF_RABBITMQ_PASSWORD=guest \
  -DCAF_RABBITMQ_HOST=192.168.56.10 \
  -jar task-submitter.jar
```

Here a workflow ID of 1 is passed to be provided on the submitted task messages. This relies on a workflow with that ID existing and accessible under the specified project ID for the workflow worker to process the received messages.

### Initialize Workflow

```
java -DCAF_TASKSUBMITTER_PROJECTID=Default \
  -DCAF_TASKSUBMITTER_DOCUMENT_INPUT_DIRECTORY=/input \
  -DCAF_TASKSUBMITTER_BASEDATA_PROCESSING_API_URL=http://192.168.56.10:19220/data-processing-service/v1 \
  -DCAF_TASKSUBMITTER_BASEDATA_WORKFLOW_INPUT_FILE=/createFiles/workflow.json \
  -DCAF_TASKSUBMITTER_BASEDATA_CREATE_BOILERPLATE=true
  -DCAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_API_URL=http://192.168.56.10:19250/boilerplateapi \
  -DCAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_INPUT_FILE=/createFiles/boilerplate.json \
  -DCAF_TASKSUBMITTER_BASEDATA_BOILERPLATE_OUTPUT_FILE=/createFiles/boilerplate_output.json \
  -DCAF_TASKSUBMITTER_BASEDATA_CREATE_CLASSIFICATION=true \
  -DCAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_INPUT_FILE=/createFiles/classification.json \
  -DCAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_API_URL=http://192.168.56.10:19290/classification/v1 \
  -DCAF_TASKSUBMITTER_BASEDATA_CLASSIFICATION_OUTPUT_FILE=/createFiles/classification_output.json \
  -DCAF_RABBITMQ_PUBLISH_QUEUE=input-queue \
  -DCAF_RABBITMQ_USERNAME=guest \
  -DCAF_RABBITMQ_PASSWORD=guest \
  -DCAF_RABBITMQ_HOST=192.168.56.10 \
  -jar task-submitter.jar
```

Here no workflow ID is provided, instead details to initialize the workflow are passed instead. Details of API's to call to create the various components of the workflow are passed alongside the input files to read the definitions from.