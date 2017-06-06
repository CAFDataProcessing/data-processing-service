# data-processing-task-receiver

An application that will monitor a set of rabbitMQ queues, retrieve messages from them and output the messages to the filesystem.

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

#### CAF_RABBITMQ_CONSUME_QUEUES

Comma separated list of queues to listen to and retrieve messages from. e.g. queue-one,queue-two,queue-three. This must be set.

### Storage Properties

This property may be specified in a file 'storage.properties' or as an environment variable.

#### CAF_STORAGE_DATA_DIRECTORY

The directory containing stored data referenced by task messages.

### Task Receiver Properties

These are properties specific to the functionality of the task receiver application. They may be specified in a file 'taskReceiver.properties' or as environment variables.

#### CAF_TASKRECEIVER_OUTPUT_DIRECTORY

The directory under which to output files for any received task message.

#### CAF_TASKRECEIVER_SAVE_TASK_DATA_ONLY

Setting for whether or not to output only the task data. The default value is false which outputs the entire message.

#### CAF_TASKRECEIVER_CLEANUP_DATASTORE_AFTER_PROCESSING

Setting for whether or not to delete storage references from the data store after processing. The default value is false which will not delete any storage references.

## Running the Application

The example below shows running the application with configuration passed as environment variables. The application will connect to rabbit on the specified host and listen for any messages placed on the queue 'result'. Messages will be taken from this queue and files with information about the messages will be output under folder specified via the taskReceiver.outputDirectory property.

```
java -DCAF_RABBITMQ_HOST=192.168.56.10 \
 -DCAF_RABBITMQ_PORT=5672 \
 -DCAF_RABBITMQ_USERNAME=root \
 -DCAF_RABBITMQ_PASSWORD=root \
 -DCAF_RABBITMQ_CONSUME_QUEUES=result \
 -DCAF_STORAGE_DATA_DIRECTORY=/store \
 -DCAF_TASKRECEIVER_OUTPUT_DIRECTORY=/output \
 -jar data-processing-task-receiver.jar
```

## Task Message Output

After receiving a task message the application outputs multiple files with information about the received task message. These will be placed beneath the output directory specified for the application in a folder named for the task ID. Sub-document files are placed in a folder named for their sub-document identifier with the parent within a 'subDocs' folder under their parent.

Typical output files are;

### Raw Task Message

The task message as received from the queue. It will be in a JSON format. This will be output as a file named 'raw_task_message'.

#### Example

Below is an example of the raw task message output file contents.

```
{"version":3,"taskId":"2-/inputFolder/HPE Ltd.docx","taskClassifier":"GenericClassifier","taskApiVersion":1,"taskData":"dGVzdA==","taskStatus":"NEW_TASK","context":{"caf/dataprocessing-fs/workflow":"dGVzdA=="},"to":"document-output","tracking":null,"sourceInfo":{"name":"GenericClassifier","version":"1.19.2"}}
```

### Task Message

This is a version of the task message that is intended to be more easily readable for a user. The task data and context are decoded from base 64 and the JSON contents are formatted. This will be output as a file named 'task_mesaage'.

#### Example

Below is an example excerpt from the task message output content.

```
{
  "version" : 3,
  "taskId" : "2-/inputFolder/HPE Ltd.docx",
  "taskClassifier" : "GenericClassifier",
  "taskApiVersion" : 1,
  "taskData" : {
    "reference" : "/inputFolder/HPE Ltd.docx",
    "childDocuments" : [ ],
    "metadata" : [ {
      "appversion" : "15.0000"
    }, {
      "CHILD_INFO_COUNT" : "0"
    }, {
...
```

### Document File

If a storageReference field is present on the received document metadata then the file will be retrieved from the data store and output. The name of this will be prefixed with 'file_'. The application will attempt to set an appropriate filename based on the available metadata fields, checking for known file name and extension fields.

### Example

Given a task data which has the below metadata;

```
"metadata" : [ {
...
{
"FILENAME" : "HPE Ltd.docx",
},
{
  "storageReference" : "cda252de-d73d-4d60-bd53-6f41007bbef2"
}
...
}]
```

The original file would be retrieved from the data store and output to a file named 'file_HPE Ltd.docx'.

A document which lacks any fields that can be used to derive filename information will be output as 'file_' with the storage reference field value appended to that name.
e.g. file_d95e8dc3-f9cd-4e1c-abcf-cb062d939a96.

### Metadata Reference Fields

Any metadata reference field in the task data that has its 'reference' field set will have its value retrieved from the data store and output to a file. The file will be named 'field_', the name of the field then a '.' and the reference for the value in the data store.

#### Example

Given the below field 'CONTENT' on received task data;

```
"metadataReferences" : {
  "CONTENT" : [ {
	"reference" : "7905117f-317e-4d1b-932f-4130d50cf1d1",
	"data" : null
  } ]
}
```

The output file name would be 'field_CONTENT.7905117f-317e-4d1b-932f-4130d50cf1d1', and contain the data stored for that reference.

### Sub-Documents

If a task message contains data on a sub-document then the files will be output to a folder structure that places the files in a 'subDocs' folder of their parent.

#### Example

Given an archive that contains an email with an attachment, the hierarchy of the file looks like;

```
archive.zip
- childArchive.zip
  - test.doc
```

The output folder structure inside the archive.zip document folder will contain the files output for archive.zip, childArchive.zip and test.doc. The structure would be similar to the below layout;
```
subDocs
  - 0
    - subDocs
      - 0
        - file
        - raw_task_message
        - task_message
    - file
    - raw_task_message
    - task_message
file
raw_task_message
task_message
```