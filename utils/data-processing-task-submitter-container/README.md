# data-processing-task-submitter-container

## Summary

This project builds a docker container that wraps the task-submitter application and starts it when the container is run.

## Configuration

The task submitter should be configured through environment variables passed to the container on startup. Details of the relevant properties can be found on the task submitter application page [here](../data-processing-task-submitter/README.md).

The container depends on a running RabbitMQ instance that it can contact to submit messages. If a workflow is to be created by the submitter rather than using an existing ID then accessible databases and APIs are required also.

The container is packaged with a default processing workflow (which uses boilerplate expressions) for use in creating a workflow when no ID is provided. This can be seen [here](./base-data/example_workflow.json). This file can be overridden using the properties of the task submitter to point to another location.
