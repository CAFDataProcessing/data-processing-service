# data-processing-family-task-submitter-container

## Summary

This project builds a Docker container that wraps the family-task-submitter application. The container will execute as configured via environment variables and then exit once a task has been sent to RabbitMQ.

## Configuration

The family task submitter should be configured through environment variables passed to the container on startup. Details of the relevant properties can be found on the family task submitter application page [here](../data-processing-family-task-submitter/README.md).

The container depends on;
* A RabbitMQ instance that can be contacted to submit messages.
* A processing API web service that can be contacted to retrieve details of the workflow to submit the task with.
* An Elasticsearch instance that can be contacted to retrieve the family of documents to submit.

## Run Configuration

The container can be ran in similar fashion to the jar file. An example command is shown below, passing environment variables and mounting a volume to store the generated workflow JavaScript. Note that the volume the JavaScript is stored to should be accessible by the workers that the task is sent to so they can retrieve the JavaScript.

```
docker run --rm \
  -e "CAF_ELASTIC_HOSTNAME=192.168.56.10" \
  -e "CAF_ELASTIC_TRANSPORT_PORT=9300" \
  -e "CAF_PROCESSING_API_URL=http://192.168.56.10:9753/data-processing-service/v1" \
  -e "FAMILY_REFERENCE=/inputFolder/Travel doc wording attached.zip:0" \
  -e "OUTPUT_QUEUE_NAME=document-input" \
  -e "OUTPUT_PARTIAL_REFERENCE=/datastore" \
  -e "PROJECT_ID=Default" \
  -e "CAF_RABBITMQ_HOST=192.168.56.10" \
  -e "CAF_RABBITMQ_PASSWORD=guest" \
  -e "WORKFLOW_ID=2" \
  -e "entityextractworkerhandler.taskqueue=dataprocessing-fs-entity-extract-in" \
  -e "familyhashingworker.taskqueue=dataprocessing-fs-family-hashing-in" \
  -e "langdetectworker.taskqueue=dataprocessing-fs-langdetect-in" \
  -v /vagrant/fsdatastore:/datastore \
  dev/cafdataprocessing/data-processing-family-task-submitter:1.4.0-SNAPSHOT
```
