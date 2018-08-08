# Overview of repository specific config options

## Setting repository configurations

A tenant can specify custom values for configuration that can then be used when creating their workflow and that are applicable only to specific repositories.
A repository configuration can only be specified for a configuration that already has a global counterpart (the key must be existing). If a global configuration does not  already exist the service will return a 405 status code with its response to make the user aware that a global configuration must be created first.
The repository configurations are always linked to a particular tenantId, other than to a repositoryId and to a key. In this way, different tenants can have different custom 
configurations for the same repository.
If a repository does not have a custom configuration set for the key provided in this call then one will be created, if one does exist then its value will be updated with the value provided.

There are two ways that a repository's custom configuration can be created, either per configuration, or in bulk.

### Per config creation:

To create a config for a user the api requires several pieces of information to create the configuration and store them for the repository, these are:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **repositoryId** - This is a unique string that identifies the repository.
3. **key** - This is the key to assign the custom configuration value to, this key must already have a corresponding global config registered in order to be accepted.
4. **value** - This is the value to set for the custom configuration.

A sample put request url would be as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config/{key}

The body of the request would then contain the string value to use when setting the repository config.

**Sample body:**

	{
	  "value": "customRepositoryConfigValue"
	}

A request issued to create a repository config will return 204 status code when successful and return no content.

### Bulk repository config creation

Repository configs can be created in bulk. This requires a message format containing an array of repository configs to set against the repository.

A sample bulk request url would be as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config

The body of the request would then contain the array of repository configs to set against the repository.

**Sample body:**

	{
	  "repositoryConfigs" [
	     {
	       "key": "CustomRepositoryConfiguration_1",
	       "value": "customRepositoryConfigValue"
	     },
	     {
	       "key": "CustomRepositoryConfiguration_2",
	       "value": "customRepositoryConfigValue"
	     }
	   ]
	}

The above request would then create each of these custom configurations against the repository provided and once complete will return a 204 status code with no content.

## Retrieving repository configuration

Retrieval of a repository's custom configuration can be done in two way similarly to setting the configuration, one on a per configuration basis and the other as a bulk.

### Per configuration retrieval

Retrieval of a specific configuration value for a repoitory requires the following information:
1. **tenantId** - This is a unique string that identifies the tenant.
2. **repositoryId** - This is a unique string that identifies the repository.
3. **key** - This is the key to use when retrieving of the custom configuration.

A sample get request url is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config/{key}

This will return the string value of the custom configuration that exists for the provided key against the provided repository.

**Sample response:**

    configurationValue

If no configuration exists for this key against this repository then the api will return a 404 not found response, this will also be accompanied by a message such as "No configuration found for this repository.".

### Bulk retrieval of repository configuration 

All of the repository specific configurations that have been set for a specific repository can be returned in one call to the api. 
This bulk request returns an array of repository configs that are assigned to the tenantId and repositoryId provided.

A sample bulk get request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config

**Sample Response:**

    [
       {
         "key": "CustomRepositoryConfiguration_1",
         "value": "customRepositoryConfigValue"
       },
       {
         "key": "CustomRepositoryConfiguration_2",
         "value": "customRepositoryConfigValue"
       },
       {
         "key": "CustomRepositoryConfiguration_3",
         "value": "customRepositoryConfigValue"
       }
    ] 

When successful this call will return a 200 response with a response body similar to that above.

## Deleting repository configuration

After custom configuration has been set for a repository that configuration can then be deleted, this, like the other operations available from this service can either be done in a per configuration or bulk way.

### Per config deletion

To delete a specific configuration for a specific repository the service will require the following information:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **repositoryId** - This is a unique string that identifies the repository.
3. **key** - The key of the configuration that should be removed.

A sample per config delete request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config/{key}

When this call completes successfully it will return a 204 status code with no content, if no configuration can be found either for that key against that repository or for that repository at all, a 404 message is return along with a suitable message.

### Bulk config deletion

A call to delete all configuration registered for a repository can be made using this api. This will remove any custom configuration that has been set for the repository.

A sample bulk delete request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/config

When this completes successfully it will return a 204 status code with no content.

## Get Effective config

This call allows for a caller to make one call that will retrieve the custom configuration value for the specified tenantId, repositoryId and key and if no custom configuration at
repository level is set, it tries to find one at tenant level, and if also this one is not found, it returns the default values of the configuration (at the global level).
Like the other calls that can be made to the API the get effective config can be used in either a per configuration or bulk mode.

### Per configuration get effective config

A call can be made to get the value of a specific configuration for a reposiory or the default if the repository has no custom value set for the configuration.

Per configuration retrieval of an effective configuration value for a repository requires the following information:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **repositoryId** - This is a unique string that identifies the repository.
3. **key** - This is the key to use in retrieval of the effective configuration.

A sample get request for the effective config is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/effectiveconfig/{key}

This will return an EffectiveRepositoryConfigValue object, this object has two properties, the first is the value, this is the value of the configuration requested. The second is the value type, the value type is an enum that can either be CUSTOM for repository or tenant specific configuration values or DEFAULT used when the repository and the tenant have no custom value set for this configuration so the default was obtained from the global config and return.


### Bulk effective config retrieval

A call can be made to retrieve all configuration that is available to a specific repository. This can be configuration specified in global configuration that is used for all repositories, at the tenant level, or the custom configurations set for the tenantId and repositoryId specified in the request, or a mix of them.

Bulk retrieval of an effective configuration value for a repository requires the following information:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **repositoryId** - This is a unique string that identifies the repository.

A sample bulk get request for effective configs is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/repositories/{repositoryId}/effectiveconfig

This will return an EffectiveRepositoryConfigs object that contains an array of repository EffectiveRepositoryConfigValue objects. Each item in the array will specify if their value is from the tenants or repository custom configuration or if it is from the default specified under the global configurations. 

**Sample Response:**

    {
      "configurations": [
        {
          "key": "ee.grammarMap",
          "value": "SomeTestValue",
          "valueType": "CUSTOM"
        },
        {
          "key": "ee.grammarMap1",
          "value": "SomeTestValue",
          "valueType": "CUSTOM"
        },
        {
          "key": "ee.grammarMap2",
          "value": "SomeTestValue",
          "valueType": "CUSTOM"
        }
      ]
    }

This call will return a 200 status code when completed successfully.
