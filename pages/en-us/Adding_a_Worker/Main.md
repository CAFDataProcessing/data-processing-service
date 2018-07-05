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
+ [Additional Documentation](#additional-documentation)


## Overview

The process of adding a worker to the default Data Processing workflow consists of several steps:

+ Add the worker to the Docker Compose file that defines the set of services used in Data Processing.
+ Update the Docker Compose overlay file that provides additional debug settings for Data Processing services.
+ Update the Data Processing workflow definition to include the new worker.

A compose file that deploys components is available [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/tree/deploy) (requires Enterprise Edition license).


## Adding a Worker to the Docker Compose File

The stack of services that are used in the default Data Processing workflow is defined by the default workflow's Docker Compose file which can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/tree/deploy).

In order for the new worker to be usable in the default Data Processing workflow it must be a Document Worker (or accept a Document Worker Composite task message) and a new section will have to be added for the worker in this Docker Compose file. A useful starting point is to identify a worker that is already defined in this file and copy its definition; then make changes to the copy to suit the new worker.

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

To aid the monitoring of running services in a default Data Processing deployment, the default Docker Compose file has an associated overlay file that defines certain port mappings and logging settings. This Docker Compose overlay file can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/blob/deploy/end-to-end-workflow/docker-compose.debug.yml).

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

Adding the new worker to the Docker Compose files makes the worker available for use in Data Processing but to actually direct documents through the worker we must add a section to the Data Processing workflow definition. The default Data Processing workflow definition can be found [here](https://github.houston.softwaregrp.net/caf/data-processing-service-internal-deploy/blob/deploy/end-to-end-workflow/processing-workflow.json).

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
    "typeName": "ChainedActionType",
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
    "typeName": "ChainedActionType",
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

The `typeName` in this definition must be `ChainedActionType`, which is registered as the `internal_name` of the Action Type used to chain Document Worker actions together.

If your worker has any preconditions that must be satisfied for it to perform its work on a document then these may be specified in the `actionConditions` section. In the above example, the Translation worker requires a CONTENT value to be present on the document.

## Additional Documentation

Having made the required changes to Docker Compose files and the workflow definitions, these changes may be tested by following the Run Services steps outlined [here](../Getting-Started).