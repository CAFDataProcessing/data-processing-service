---
layout: default
title: Creating an Action
---

# Creating an Action

New actions can be created to extend the capabilities of the data processing service. You may want to integrate a new capability to translate
text from one language to another, or you may want to use the existing metadata of the document to lookup additional information about the document.

The Document Worker Framework is the perfect solution to quickly create new actions for the Data Processing Service.

A document enters a document processing workflow with metadata, each action in the workflow can add, remove and modify this metadata.
The document worker standardises this process by formalising the input and output of an action based on the Document Worker.
The input to such a worker is defined as the metadata fields that already exist on the document.
The output is a series of modifications to the documents metadata.

Creating an action using the Document Worker Framework is achieved by using the Document Worker Archetype.
The archetype scaffolds a simple example data processing action that can be used as the basis for a new data processing action.
Further integration tasks such as the creation of a handler and converter are not required.

## Contents

+ [Overview](#overview)
+ [Getting Started](#getting-started)
+ [Document Worker Error Handling](#document-worker-error-handling)
+ [Additional Documentation](#additional-documentation)

## Overview

DocumentWorker in Data Processing :

+ Get started by creating a Document Worker from the Document Worker Archetype.
+ Configure a Document Worker to process documents in bulk, or one at a time.
+ Enrich a document through implementations which handle a documents fields, and field values.

## Getting Started

### Create a Document Worker

Documentation on how to create a Document Worker from the Document Worker Archetype can be found [here](WorkerArchetypeUsage).

A Document Worker created from the archetype will contain an example implementation. This implementation is used to look at a document for
a field name 'REFERENCE', and then to use its value to retrieve data from a table. The retrieved data is then applied to the value of the field 'UNIQUE_ID'.

The example implementation implements the 'BulkDocumentWorker' interface, so that documents can be processed in batches. When using the
BulkDocumentWorker interface be sure to implement the 'processDocument' method as well as the 'processDocuments' method. The reason for
this is that when processing documents as a batch, if an error occurs when processing one of the documents, each document will then be
retried one at a time and will utilize the 'processDocument' method.

If there is no efficiency to be gained by processing documents together then the 'DocumentWorker' interface should be implemented instead
of the 'BulkDocumentWorker' interface, and the method 'processDocuments' should be removed. The example implementation implements
BulkDocumentWorker for demonstration purposes only.

### Control Configuration Options

The Document Worker defines the following control options:

<table>
    <tr>
        <td><b>Name</b></td>
        <td><b>Description</b></td>
    </tr>
    <tr>
        <td>maxBatchSize</td>
        <td>This is a property of type 'int'. Specifies the maximum number of documents to include in a batch.</td>
    </tr>
    <tr>
        <td>maxBatchTime</td>
        <td>This is a property of type 'long'. Specifies the maximum length of time (in milliseconds) to build up a batch. The time starts as soon as the first document is received.</td>
    </tr>
    <tr>
        <td>closeBatch()</td>
        <td>Draws this batch of documents to a close. Calling this method will result in no new documents being added to the batch, the 'currentSize' will remain the same. This could be implemented to allow for some dynamic handling of documents. After calling this method, it is considered good practice to continue to iterate over the documents in the batch until there are none left to be processed.</td>
    </tr>
</table>

### Document Worker Advantages Over Other Workers

Document workers have the following advantages:

+ A Document Worker is simpler to implement, through utilizing the Document Worker Archetype.
+ Normal workers are task oriented. If tasks align with documents, a Document Worker is better suited because it makes available interfaces which are designed to handle documents.
+ Document Workers integrate more simply into the the data processing chain. There is no need to write extra components such as a handler and a converter, because these are already written.

### How can a Bulk Document Worker be more Advantageous?

When the data processing action supports batching the Bulk Document Worker is advantageous. For example a Document Worker can be used to
lookup and retrieve information from a database. If field values on a document contained project IDs and these values where to be replaced
with their corresponding project names. A worker could take a batch of documents and make a single call to a database to perform a bulk
lookup of project IDs, and then to return a project name and the project ID.

These documents will be processed in a single interaction with the database. If you attempted to process each of the documents
individually latency would be greatly increased because multiple round trips would be made to the database.

Utilising the BulkDocumentWorker interface allows users to make use of the 'closeBatch()' method. Calling this method will result in no
more documents being added to the batch. This method can be implemented to allow for dynamic handling of documents. For example, if the
accumulated size of each document where to exceed a limit, the closeBatch() method can prevent any more documents from being added
to the batch, regardless of how 'maxBatchSize' and 'maxBatchTime' are configured, in order to reduce the time spent retrieving
information from the database.

Additionally, if an implementation of BulkDocumentWorker was to encounter a problem during processing of the batch. The worker will stop
processing the documents as a batch and begin processing every document individually. This is very useful because it means that one
troublesome document in a batch will not stop other documents from being processed.

#### Summary

New actions can be created to extend the capabilities of the data processing service.
Creating an action using the Document Worker Framework is achieved by using the Document Worker Archetype.
A Document Worker can be used to alter the field names and values contained within the metadata of a document supplied to it.
Documents can be processed as a batch.

## Document Worker Error Handling

Explicit failures should be added to documents as they are processed. Failures are recorded on the document by using the
`addFailure` method on the `Document` object. The `addFailure` method takes two `String` arguments: a failure ID, and a
failure message. The failure ID should be a non localizable identifier related to the failure. The failure message
should be a human readable message relating to the failure.

An example failure ID is: "EXTRACT_FAILURE_0001".
An example failure message is: "Failed to extract text due to Keyview error: KVERR_FormatNotSupported".

When utilising BulkDocumentWorker, if an error is encountered whilst processing a batch of documents, the worker will stop processing the
documents as a batch and begin processing each document individually. This occurs so that a troublesome document in a batch will not stop
other documents from being processed.

A Document Worker can throw a `DocumentWorkerTransientException`. This should be thrown when a transient failure has occurred such as a
brief disconnection from a temporary resource such as a database. The operation might be able to succeed if it is retried at a later time,
so the task will be pushed back onto the queue. The 'retryLimit' is set in the configuration file for `RabbitWorkerQueueConfiguration`.

WorkerFramework supports error handling in the case of non parsable input messages, and catastrophic errors which are not recoverable.
This information as well as information on poison messages and retry counts, are documented in [Worker Framework](https://workerframework.github.io/worker-framework/index.html).

## Additional Documentation

JavaDocs for Document Worker Interface can be found [here](apidocs/index).