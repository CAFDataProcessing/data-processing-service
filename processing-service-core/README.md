# processing-service-core

NodeJS implementation of data-processing-service contract. Uses the swagger-node library to provide validation against a yaml contract for requests and responses.

## Overview

The data-processing-service is a NodeJS implementation of the swagger data-processing-service-contract. It implements those methods as calls to a Policy API instance, where a single call on the contract may be represented as multiple calls to the Policy API, creating structures that can be used for data processing by components that support the Policy API. This project aims to simplify the interactions required with the Core Policy API to create and manage the objects required for data processing.

## Pre-Requisites

An http contactable Core Policy API instance that this API may call.

## Detailed Overview

A more detailed overview of the implementation is available [here](./Architecture.md).

## Quick Start

### Run UI install

Execute the maven install goal on the [processing-service-ui](../processing-service-ui) project. This will copy a modified version of the swagger-ui containing the processing-service contract to the ui folder of this directory.

## Developer Quick Start
### Install using Source Code
For developers it is recommended to install and run the project with swagger-node directly. This is useful as when changes are made to files the server will be reloaded automatically.

1. Check out the project.
2. Install swagger-node

    ```
    npm install -g swagger
    ```
3. From the project directory run the below command to install the dependencies via node package manager.

    ```
    npm install
    ```
4. Run the swagger project start command to launch the server.

    ```
    swagger project start
    ```    
    
## UI

An HPE Swagger UI is available based on the UI generated in the data-processing-service-contract repository [here](https://github.com/CAFDataProcessing/data-processing-service/tree/develop/processing-service-contract).

For a deployed service the UI is available at;

```
http://<service.ip.address>:<port>/data-processing-ui
```

Replace <service.ip.address> and <port> as necessary.

## Configuration

Configuration is achieved via environment variables.

### General Service Configuration

#### CAF_PROCESSING_SERVICE_PORT
The port that the service will run on. Defaults to 8080.

#### CAF_PROCESSING_SERVICE_CACHE_DURATION
The time in seconds that cached data should be retained. Defaults to 600.

### Policy API Service Configuration

#### CAF_PROCESSING_SERVICE_POLICY_API_HOST
The host that the Policy API is deployed to. Defaults to localhost.

#### CAF_PROCESSING_SERVICE_POLICY_API_PORT
The port that the Policy API is running on. Defaults to 9000.

#### CAF_PROCESSING_SERVICE_POLICY_API_ENTRY_PATH
The url path that all calls to Policy API should start with. Defaults to '/corepolicy'.
e.g. To create a classification API would by default be contacted at http://localhost:9000/corepolicy/classification/create/

### Logging Configuration

#### CAF_LOG_LEVEL
Controls the minimum level of log message that will be output by service.
- DEBUG: Lowest level, intended for diagnosing behaviour of the service.
- INFO: General messages that may be of interest about the operation of the service. This is the default level if none if not configured.
- WARNING: Occurrences in the service that may require attention but are not determined to be detrimental to operation of the service.
- ERROR: For problems that occur in the service that impede correct function.