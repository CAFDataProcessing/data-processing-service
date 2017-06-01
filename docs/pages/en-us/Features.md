---
layout: default
title: Features

banner:
    icon: 'assets/img/data-processing-graphic.png'
    title: Data Processing Service
    subtitle: Analyze a Larger Range of Formats
    links:
        - title: GitHub
          url: https://github.com/CAFDataProcessing/data-processing-service
---

# Key Features

## Microservice Architecture

Rather than a monolithic application aiming to service all processing needs, Data Processing promotes tailored microservices that can be plugged into a deployment as required. The benefits of this approach include:

* A consumer of Data Processing can decide which services their solution requires, thus avoiding unnecessary deployment and setup of irrelevant components. For example:
  * In a system that only groups the files that have been processed, you would not deploy the classification worker, API and required database.
  * In a system that only ingests text documents, you would not need the OCR and Speech worker to convert content to text.

* Each processing service maintains its own independent input queue and resource allocation. Data sources that require more time to process will not slow the processing of other, less intensive data. For example:
  * An hour long audio file requiring conversion to text will have no effect on the throughput of text or image files as the workflow worker will send the audio file to the speech worker's input queue and the others to the text extract and OCR worker queues. While the speech worker is processing that audio file, the other workers continue processing their files and can receive new files allocated to them by the workflow worker.
  
* New services are created independently of the overall Data Processing package, allowing for faster development, testing and debugging of a component. Integration into the larger suite occurs by following a defined pattern used with existing components. For example:
  * A new copy worker may be developed using the Worker Framework without setting up the other Data Processing components. Messages can be passed to this worker's input queues through custom test tools and output is similarly verified. On completion of the worker, it may be included in the Data Processing suite and invoked by the workflow worker through the writing of a handler and converter and setting up an appropriate action. Alternatively if the copy worker is implemented as a CAF Document Worker then the document worker handler/converter may be used to communicate with the new worker.

## Elastic Scaling

* Individual components scale up or down as demand for their processing type fluctuates, thus helping to facilitate efficient usage of resources. This scaling can be performed manually or by another service, such as the CAF Auto Scaler. For example:
  * When the current input into Data Processing is solely text files, you have no need of the speech worker. That service scale down to no running instances through observation of its input queue, which frees resources. When audio files begin to enter the system, messages routed to the speech worker input queue start to form a backlog, which is recognized and causes instances of the speech worker service to launch.
  * If the number of messages on the speech worker input queue increases faster than the output of the worker, then additional instances of the speech worker service can be launched and process messages on the input queue until the input rate decreases (at which point those additional worker instances can scale down).

* Services are available as Docker containers, which allows launching instances across multiple machines and easily managing resource allocation.

## Decision-based Processing

Using the Data Processing API and the workflow worker, you can control the operations performed on files using conditions against the data passed in as well as the result of any processing that has occurred to that point. This approach allows a single input entry point for data into the system and puts the responsibility of routing data to the next appropriate processing operation on the workflow worker. A basic processing workflow may consist of an action that: 

  * extracts content for audio files using the speech worker.
  * extracts the content for image files using the OCR worker.
  * extracts the content for text files using the text extract worker.
  * finds the language used in the content.
  * sends the result to a message queue for another application to retrieve.
  
The Data Processing API supports a wide variety of conditions to control processing flow, with support for conditions against individual actions or a group of actions in a rule. More detail on the conditions available can be found in the Data Processing API [here](Data_Processing/API) under models.

## Integration with CAF Storage Service

Processing data often involves dealing with large pieces of data. To accommodate this requirement, Data Processing workers have support for references to data stored using a CAF storage implementation. Large data values and files themselves can be uploaded to a storage location and a reference to the stored data is then passed into the workers that will retrieve data and perform operations on it.

## Variety of Processing Operations

The processing of data is supported through specialized workers. Some processing operations are only available with the Enterprise Edition, for details on obtaining an Enterprise Edition license please raise an issue [here](https://github.com/CAFDataProcessing/data-processing-service/issues). Current features include the following.

### OCR

Optical Character Recognition is performed on image files to extract text from images and place it in new fields. *Enterprise Edition* only.

### Speech to Text

Audio files are transcribed to written text representations. *Enterprise Edition* only.

### Text Extraction

The content of text-based documents is extracted. *Enterprise Edition* only.

### Metadata Extraction

Metadata for files is extracted and added as fields in the data. Metadata includes: 

* Generic file metadata, such as name, size,  and MIME Type of files. 
* File type specific properties, for example:
  * Email properties, like received data, sent date, to, from.
  * Image file properties, such as height, width, and bit depth.
  
*Enterprise Edition* only.

### Binary Hash Generation

Binary hashes can be generated for each file processed and output in the collection of data for the file.
  
### Expansion of Archives

Archive files can be passed in as a single file and then extracted with a specified depth setting. All processing operations are performed against extracted sub-files. *Enterprise Edition* only.

### Entity Detection

Concepts like address, phone number, and social security number are detected and, optionally, obfuscated within documents. It is also possible to supply user-defined entities in addition to the built-in entities. *Enterprise Edition* only.

### Content Redaction and Extraction

Regular expressions can be used to detect, redact and extract pieces of content.

### Language detection.

Text can be analysed to detect and return the top three languages present.

### Unique ID Tagging

Each item passed into Data Processing can have a unique identifier assigned that may be used as the ID for the built up data representation when processing completes, for example, as the ID to use when indexing the processing result into an archive platform. Files extracted from archive formats can also be assigned unique identifiers. *Enterprise Edition* only.

### Classification

Data can be compared against defined classifications, which represent sets of conditions. Any matches will be recorded on the data and output at the end of processing, allowing for custom groupings, such as Travel Documents or Receipts, to be built from processed items.

### Key Content Segregation for Emails.

An email conversation can have its individual message content separated, with support for defining the ranges that make up the primary, secondary and tertiary messages.

### Email Signature Detection.

Support for the detection of signatures in email content and removal of signatures from that content.

## Extensibility with Custom Processing Actions

New processing actions can be developed and added to already deployed systems through deployment of a new worker to service the processing operation and the writing of plugin classes to handle sending a task from the workflow worker to the new worker and to convert the result to fields on the item being processed. Once this is done, new actions can be created that reference the new processing action type and included in existing processing pipelines.
