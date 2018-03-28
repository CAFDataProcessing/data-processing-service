# Overview of global configuration options

## Setting global configurations

Parameters required to create or update global configuration:

- **key** (max length = 255) - assigns the global configuration value,
- **default** - global value to set for the key,
- **description** (optional) - description for the global configuration,
- **scope** - defines the scope of use for the global config key. If the scope is set to 'TENANT' then it may be overridden by tenant configurations. Currently, scope value can be set only to "TENANT".


A sample **put** request url is as follows:

	http://<host>:<port>/data-processing-service/v1/globalConfig/{key}

The body of the request would then contain the strings *default*, *description* and *scope*.

**Sample body:**

	{
	  "default": "string",
	  "description": "string",
	  "scope": "TENANT"
	}

A request issued to create or update a global configuration can return:

- **204** status code - when successful and returns no content,
- **400** status code - when invalid key is used or some of required properties is missing in the body of the request.

## Retrieving global configuration

Parameters required to retrieve global configuration:

- **key** (max length = 255) - identifies the global configuration value.

A sample **get** request url is as follows:

	http://<host>:<port>/data-processing-service/v1/globalConfig/{key}

A request issued to get a global configuration can return:

- **200** status code - when successful and returns an object representation of the global configuration that exists for the provided key,
- **400** status code - when invalid key is used,
- **404** status code - when the key is not found.

## Retrieval of all global configurations

No parameters are required to retrieve all global configurations.

A sample **get** request url is as follows:

	http://<host>:<port>/data-processing-service/v1/globalConfig/

A request issued to get all global configurations can return:

- **200** status code - when successful and returns an array of all global configurations or an empty array,

## Deleting global configuration

Parameters required to delete global configuration:

- **key** (max length = 255) - identifies the global configuration value.

A sample **delete** request url is as follows:

	http://<host>:<port>/data-processing-service/v1/globalConfig/{key}

A request issued to delete a global configuration can return:

- **200** status code - when successful and returns no content,
- **400** status code - when invalid key is used,
- **404** status code - when the key is not found.

