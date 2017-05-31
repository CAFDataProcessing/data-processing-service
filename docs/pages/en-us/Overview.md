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
- Unique ID tagging of files. *Enterprise Edition* only.
- Classification of data into groups.
- Key content segregation for emails.
- Email signature detection.
- Binary Hash generation for files.

### Components

The sections that follow describe the components of Data Processing.

#### Data Processing API

The Data Processing API is a RESTful web service to create  pipelines (workflows in the API) that control how to handle data coming into the system. It allows for the definition of processing operations (actions) to perform along with criteria controlling which operations occur against specific data.

The Web methods available in the Data Processing API are detailed in the [API](Data_Processing/API) section.

#### Data Processing Workers

Built on the Worker Framework infrastructure, these workers are applications that read messages from queues, perform application-specific processing using information provided in the message, and output a result to another queue for pickup. There are many available processing workers detailed in the [Features](Features) section, for example, OCR and language detection. A Data Processing worker of particular note is the workflow worker.

##### Workflow Worker

This worker is responsible for coordinating how items coming into the Data Processing environment should be processed. Each message read from it's input queue specifies a particular workflow to execute against that item, which is defined using the Data Processing API. The worker evaluates the item against the defined rules and actions of that workflow and, when the criteria for an action is met, it sends a task to the appropriate microservice application's input queue. For example, you might set a condition of file type matching jpeg on an action to perform OCR processing. After disposing of an item, the worker proceeds with the next message on it's input queue.
When processing on the other application completes, the result is sent to the input queue of the workflow worker, which then applies the result to the input message originally passed. It proceeds to the next action in the rule and repeats the cycle. The final built-up representation can be sent to a specified queue for another application.

By isolating this processing decision logic as its own component, you can achieve higher throughput as those items requiring longer to process, such as an hour long audio file, do not block less complex files, such as text documents, from being processed in the interim. Instead, files are sent to their processing-specific application and, after processing, return to the workflow worker to determine the next action to take on the item.

### Example Data Processing Use Cases

Using the previously developed services in the suite, you can peform a wide range of processing against passed data. Here are some examples:

- Analysing the languages used in a set of audio files and grouping them based on this information.
- Redaction of sensitive information from documents in compliance with retention periods.
- Removal of unnecessary junk information from files before archival.
- Uniquely tagging large volumes of files.
- Separation of files into redundant, obsolete and trivial categories based on user-defined criteria for an action to be taken.

The capabilities of Data Processing continue to grow with new services being developed by both the Data Processing team and its consumers.
