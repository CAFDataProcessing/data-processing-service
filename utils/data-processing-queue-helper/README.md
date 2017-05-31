# data-processing-queue-helper

## Summary

Utility project to assist in publishing/receiving messages to/from a rabbitMQ queues.

## Configuration

The library can be configured through any one of the followed ways;

* a file on the classpath 'rabbit.properties'
* a file 'rabbit.properties' in a folder specified by the environment variable 'CAF_CONFIG_LOCATION'
* specifying configuration properties as environment variables

### Properties

#### CAF_RABBITMQ_HOST

Hostname where rabbitMQ is running.

#### CAF_RABBITMQ_PORT

Port to communicate with rabbitMQ to publish/receive messages.

#### CAF_RABBITMQ_USERNAME

Username to use for rabbitMQ application.

#### CAF_RABBITMQ_PASSWORD

Password to use for rabbitMQ application.

#### CAF_RABBITMQ_MAX_ATTEMPTS

Number of times to try and establish connection to rabbitMQ if unable to connect. Default to '-1' which indicates infinite retry attempts will be made.

#### CAF_RABBITMQ_BACKOFF_INTERVAL

The time (in seconds) between re-attempts if communication with RabbitMQ fails for any reason. Increases with subsequent failures. Defaults to 5.

#### CAF_RABBITMQ_MAX_BACKOFF_INTERVAL

The maximum time (in seconds) between re-attempts if communication with RabbitMQ fails for any reason. Defaults to 15.

#### CAF_RABBITMQ_PUBLISH_QUEUE

The queue to publish messages to.

#### CAF_RABBITMQ_CONSUME_QUEUES

Comma separated list of queues to listen to and retrieve messages from to send to listeners. e.g. queue-one,queue-two,queue-three.
