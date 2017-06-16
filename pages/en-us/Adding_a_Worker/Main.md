---
layout: default
title: Adding a Worker
---

# Adding a Worker

Additional workers may be added to the default Data Processing workflow. 

## Contents

+ [Overview](#overview)
+ [Adding a Worker to the Docker Compose File](#adding-a-worker-to-the-docker-compose-file)
+ [Updating the Debug Docker Compose File](#updating-the-debug-docker-compose-file)
+ [Updating the Data Processing Workflow Definition](#updating-the-data-processing-workflow-definition)
+ [Updating the Minimal Data Processing Deployment](#updating-the-minimal-data-processing-deployment)
+ [Additional Documentation](#additional-documentation)


## Overview

The process of adding a worker to the default Data Processing workflow consists of several steps:

+ Add the worker to the Docker Compose file that defines the set of services used in Data Processing.
+ Update the Docker Compose overlay file that provides additional debug settings for Data Processing services.
+ Update the Data Processing workflow definition to include the new worker.

A compose file that deploys only open source components is available [here](https://github.com/CAFDataProcessing/data-processing-service-deploy). If the new worker is open source then it may be added to this file if required. Otherwise it should be added to the Enterprise Edition compose file [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy).

For Enterprise Edition, in addition to the default data Processing workflow, there is also a "minimal" form of the workflow which may also be updated with the new worker if required.


## Adding a Worker to the Docker Compose File

The stack of services that are used in the default Data Processing workflow is defined by the default workflow's Docker Compose file which can be found [here](https://github.com/CAFDataProcessing/data-processing-service-deploy).

In order for the new worker to be usable in the default Data Processing workflow, a new section will have to be added for the worker in this Docker Compose file. A useful starting point is to identify a worker that is already defined in this file and copy its definition; then make changes to the copy to suit the new worker.

The `langDetectWorker` definition is a straightforward example to copy:
 
```
  langDetectWorker:
    depends_on:
      - rabbitmq
    image: cafdataprocessing/worker-languagedetection:2.0.0
    env_file:
      - ./rabbitmq.env
    environment:
      CAF_WORKER_DATASTORE_PATH: /dataStore
      CAF_WORKER_INPUT_QUEUE: dataprocessing-fs-langdetect-in
      CAF_WORKER_OUTPUT_QUEUE: document-input
    volumes:
      - worker-datastore:/dataStore
```

Let's say we have a new worker that performs some translation process and we want to add this worker to the default Data Processing definition. Copy and paste the `langDetectWorker` section of the Docker Compose file, then make changes to the copied section to suit the new worker as follows:
 
```
  translationWorker:
    depends_on:
      - rabbitmq
    image: worker-translation:1.0.0
    env_file:
      - ./rabbitmq.env
    environment:
      CAF_WORKER_DATASTORE_PATH: /dataStore
      CAF_WORKER_INPUT_QUEUE: dataprocessing-fs-translation-in
      CAF_WORKER_OUTPUT_QUEUE: document-input
      TRANSLATION_TARGET_LANGUAGE: Irish
    volumes:
      - worker-datastore:/dataStore
```

If this worker has additional configuration settings that may be controlled by environment variables then these may be defined in the `environment` section of the worker's Docker Compose definition. Similarly, any volumes and service dependencies may also be added as required.

## Updating the Debug Docker Compose File

To aid the monitoring of running services in a default Data Processing deployment, the default Docker Compose file has an associated overlay file that defines certain port mappings and logging settings. This Docker Compose overlay file can be found [here](https://github.com/CAFDataProcessing/data-processing-service-deploy/blob/develop/docker-compose.debug.yml).

Again, copy and paste the section relating to the `langDetectWorker` and modify it for the new worker to ensure that the worker's admin port is accessible externally and that it logs at the desired log level.

In the port mapping, be careful to set its external port to one that is not in use by other services; to aid in doing so, it is preferable to place the section for the new worker at the end of the file, with its external port number simply incrementing the previous worker's:

```
  translationWorker:
    ports:
      - "9572:8081"
    environment:
      CAF_LOG_LEVEL: DEBUG
```

## Updating the Data Processing Workflow Definition

Adding the new worker to the Docker Compose files makes the worker available for use in Data Processing but to actually direct documents through the worker we must add a section to the Data Processing workflow definition. The default Data Processing workflow definition can be found [here](https://github.com/CAFDataProcessing/data-processing-service-deploy/blob/develop/processing-workflow.json).

Again, the simplest approach is to copy the relevant workflow section for the `LangDetect` worker:

```
{
    "name": "LangDetect",
    "description": "Detects top 3 languages in an uploaded document.",
    "order": 700,
    "settings": {
        "workerName": "worker-languagedetection",
        "queueName": "dataprocessing-fs-langdetect-in",
        "fields": ["CONTENT"]
    },
    "typeName": "DocumentWorkerHandler",
    "actionConditions": [{
            "name": "CONTENTexists",
            "additional": {
                "type": "exists",
                "field": "CONTENT",
                "notes": "CONTENT exists: CONTENTexists"
            }
        }]
},
```

Before pasting the copied worker definition, decide where in the workflow you think it best to place the new worker. It is strongly recommended that the order of workers listed in the workflow definition accurately reflects their logical order in the processing performed by the workflow.

In the case of our translation worker, let's say that we would like it to perform its work after `LangDetect` and before `Boilerplate`, so paste the copied section between these workers. In this case, we ensure that the new worker's `order` value reflects its position in the workflow, so we set it to a value greater than the order for `LangDetect` and less than the order for `Boilerplate`:

```
{
    "name": "Translate",
    "description": "Translates text to a specified target language",
    "order": 750,
    "settings": {
        "workerName": "worker-translation",
        "queueName": "dataprocessing-fs-translation-in",
        "fields": ["CONTENT"]
    },
    "typeName": "DocumentWorkerHandler",
    "actionConditions": [{
            "name": "CONTENTexists",
            "additional": {
                "type": "exists",
                "field": "CONTENT",
                "notes": "CONTENT exists: CONTENTexists"
            }
        }]
},
```

The `typeName` in this definition must be the "short" name of the Policy Handler for this type of worker, which is registered as the `internal_name` of the corresponding Action Type. It is likely that your new worker will have been developed as a Document Worker, in which case the correct handler typeName to specify here will be `DocumentWorkerHandler`.

If your worker has any preconditions that must be satisfied for it to perform its work on a document then these may be specified in the `actionConditions` section. In the above example, the Translation worker requires a CONTENT value to be present on the document.

## Updating the Minimal Data Processing Deployment

For Enterprise Edition, in addition to the default Data Processing workflow and the services that it requires, there is also a minimal Data Processing workflow.

If it is desirable to include the new worker in the minimal workflow as well as the default workflow, then similar changes need to be made in the minimal workflow definitions. The minimal Data Processing definitions can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/tree/develop/minimal).

### Add the worker to the minimal workflow's Docker Compose file

The minimal Data Processing workflow's Docker Compose file can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/blob/develop/minimal/docker-compose.yml).

The changes to make in this file are analogous to those made for the default definition, as described in [Adding a Worker to the Docker Compose File](#adding-a-worker-to-the-docker-compose-file).

### Add the worker to the minimal workflow's debug Docker Compose file

The minimal Data Processing workflow's debug Docker Compose overlay file can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/blob/develop/minimal/docker-compose.debug.yml).

The changes to make in this file are analogous to those made for the default definition, as described in [Updating the Debug Docker Compose File](#updating-the-debug-docker-compose-file).

### Add the worker to the minimal workflow's definition

The minimal Data Processing workflow definition can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/blob/develop/minimal/processing-workflow.json).

The changes to make in this file are analogous to those made for the default workflow definition, as described in [Updating the Data Processing Workflow Definition](#updating-the-data-processing-workflow-definition).

## Additional Documentation

Having made the required changes to Docker Compose files and the workflow definitions, these changes may be tested by following the Run Services steps outlined [here](../Getting-Started).