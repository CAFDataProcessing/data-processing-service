# data-processing-task-receiver-container

## Summary

This project builds a docker container that wraps the task-receiver application and starts it when the container is run.

## Configuration

The task receiver should be configured through environment variables passed to the container on startup. Details of the relevant properties can be found on the task receiver application page [here](../data-processing-task-receiver/README.md).

The container depends on a running RabbitMQ instance that it can contact to retrieve messages and a writable location to be specified for the output of message files.
