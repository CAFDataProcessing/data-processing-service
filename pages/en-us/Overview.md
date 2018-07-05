---
layout: default
title: Overview

banner:
    icon: 'assets/img/data-processing-graphic.png'
    title: Data Processing Service
    subtitle: Analyze a Larger Range of Formats
    links:
        - title: GitHub
          url: https://github.com/CAFDataProcessing/data-processing-service
---

# Overview

Data Processing is a suite of components that facilitates the extraction, analysis and modification of data.
The wide variety of data sources available today makes data analytics and operations on data a challenging problem. Data Processing provides a flexible, elastic approach to solve this problem by allowing you to develop specialized services and include a set of processing actions to be conditionally performed against the data.

The services in Data Processing are available as Docker containers for faster and simpler deployment. You can elect to only include those services applicable to your needs, allowing for a minimum footprint without unnecessary resource allocation. You can also manage each application separately with additional instances spun up to meet higher workload demands or scaled back when not required.

For instructions on deploying Data Processing services, see [Getting Started](Getting-Started).

### Sample Processing Operations

The following operations are available through Data Processing. Some processing operations are only available with the Enterprise Edition, for details on obtaining an Enterprise Edition license please raise an issue [here](https://github.com/CAFDataProcessing/data-processing-service/issues):

- OCR on image files. *Enterprise Edition* only.
- Conversion of audio to text. *Enterprise Edition* only.
- Extraction of archives. *Enterprise Edition* only.
- Entity extraction for detection of known concepts such as addresses, phone numbers, and social security numbers. *Enterprise Edition* only.
- Content redaction.
- Language detection.
- Key content segregation for emails.
- Email signature detection.
- Binary Hash generation for files.

### Components

The sections that follow describe the components of Data Processing.

#### Data Processing API

The Data Processing API is a RESTful web service to create  pipelines (workflows in the API) that control how to handle data coming into the system. It allows for the definition of processing operations (actions) to perform along with criteria controlling which operations occur against specific data.

The Web methods available in the Data Processing API are detailed in the [API](Data_Processing/API) section.

#### Data Processing Workers

Built on the Worker Framework infrastructure, these workers are applications that read messages from queues, perform application-specific processing using information provided in the message, and can output a result to another queue for pickup. There are many available processing workers detailed in the [Features](Features) section, for example, OCR and language detection. A Data Processing worker of particular note is the workflow worker.

##### Workflow Worker

This is the first stop in evaluating an item against a workflow. The worker is responsible for retrieving a specified workflow and creating a script representation of the workflow that an item can be evaluated against to determine the next worker the item should be sent to. This generated script is then passed as part of the task so that when a Data Processing worker completes its processing it can evaluate the updated item against the workflow script and determine the next worker to send a processing task to for the item. This creates a chain of task messages being processed and evaluated until the workflow is completed. As the item flows from worker to worker its has a list of changes that each worker makes, such as the addition, setting or removal of fields on the item.

The evaluation of the item involves comparing it against a defined set of rules and actions and sending a task to the worker described by the first action whose criteria matches the item. For example, you might set a condition of file type matching jpeg on an action which specifies a queue of a worker to perform OCR processing. After sending an item to the appropriate queue, the worker proceeds with the next message on it's input queue.

### Example Data Processing Use Cases

Using the previously developed services in the suite, you can peform a wide range of processing against passed data. Here are some examples:

- Analysing the languages used in a set of audio files and grouping them based on this information.
- Redaction of sensitive information from documents in compliance with retention periods.
- Removal of unnecessary junk information from files before archival.
- Separation of files into redundant, obsolete and trivial categories based on user-defined criteria for an action to be taken.

The capabilities of Data Processing continue to grow with new services being developed by both the Data Processing team and its consumers.
