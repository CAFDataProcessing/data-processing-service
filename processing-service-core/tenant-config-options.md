# Overview of tenant specific config options

## Setting tenant configurations

A tenant can specify custom values for configuration that can then be used when creating their workflow.
A tenant can only specify a custom configuration value for a configuration that already has a global counterpart. If a global configuration does not  already exist the service will return a 405 status code with its response to make the user aware that a global configuration must be created first.
If a tenant does not have a custom configuration set for the key provided in this call then one will be created for the tenant, if one does exist then its value will be updated with the value provided.

There are two ways that a tenant's custom configuration can be created, either per configuration, or in bulk.

### Per config creation:

To create a config for a user the api requires several pieces of information to create the configuration and store them for the tenant, these are:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **key** - This is the key to assign the custom configuration value to, this key must already have a corresponding global config registered in order to be accepted.
3. **value** - This is the value to set for the custom configuration.

A sample put request url would be as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config/{key}

The body of the request would then contain the string value to use when setting the tenant config.

**Sample body:**

	{
	  "value": "customTenantConfigValue"
	}

A request issued to create a tenant config will return 204 status code when successful and return no content.

### Bulk tenant config creation

Tenant configs can be created in bulk. This requires a message format containing an array of tenant configs to set against the tenant.

A sample bulk request url would be as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config

The body of the request would then contain the array of tenant configs to set against the tenant.

**Sample body:**

	{
	  "tenantConfigs" [
	     {
	       "key": "CustomTenantConfiguration_1",
	       "value": "customTenantConfigValue"
	     },
	     {
	       "key": "CustomTenantConfiguration_2",
	       "value": "customTenantConfigValue"
	     }
	   ]
	}

The above request would then create each of these custom configurations against the tenantId provided and once complete will return a 204 status code with no content.

## Retrieving tenant configuration

Retrieval of a tenant's custom configuration can be done in two way similarly to setting the configuration, one on a per configuration basis and the other as a bulk.

### Per configuration retrieval

Retrieval of a specific configuration value for a tenant requires the following information:
1. **tenantId** - This is a unique string that identifies the tenant.
2. **key** - This is the key to use when retrieving of the custom configuration.

A sample get request url is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config/{key}

This will return the string value of the custom configuration that exists for the provided key against the provided tenant.

**Sample response:**

    configurationValue

If no configuration exists for this key against this tenant then the api will return a 404 not found response, this will also be accompanied by a message such as "No configuration found for this tenant.".

### Bulk retrieval of tenant configuration 

All of the tenant specific configurations that have been set for a specific tenant can be returned in one call to the api. 
This bulk request returns an array of tenant configs that are assigned to the tenantId provided.

A sample bulk get request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config

**Sample Response:**

    [
       {
         "key": "CustomTenantConfiguration_1",
         "value": "customTenantConfigValue"
       },
       {
         "key": "CustomTenantConfiguration_2",
         "value": "customTenantConfigValue"
       },
       {
         "key": "CustomTenantConfiguration_3",
         "value": "customTenantConfigValue"
       }
    ] 

When successful this call will return a 200 response with a response body similar to that above.

## Deleting tenant configuration

After custom configuration has been set for a tenant that configuration can then be deleted, this, like the other operations available from this service can either be done in a per configuration or bulk way.

### Per config deletion

To delete a specific configuration for a specific tenant the service will require the following information:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **key** - The key of the configuration that should be removed.

A sample per config delete request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config/{key}

When this call completes successfully it will return a 204 status code with no content, if no configuration can be found either for that key against that tenant or for that tenant at all, a 404 message is return along with a suitable message.

### Bulk config deletion

A call to delete all configuration registered for a tenant can be made using this api. This will remove any custom configuration that has been set for the tenant.

A sample bulk delete request is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/config

When this completes successfully it will return a 204 status code with no content.

## Get Effective config

This call allows for a caller to make one call that will retrieve the custom configuration value for a specified then (and configuration key if required) and if no custom configuration is set, return the default values for configurations that apply to that tenant (or a specific configuration if requested).
Like the other calls that can be made to the API the get effective config can be used in either a per configuration or bulk mode.

### Per configuration get effective config

A call can be made to get the value of a specific configuration for a tenant or the default if the tenant has no custom value set for the configuration.

Per configuration retrieval of an effective configuration value for a tenant requires the following information:

1. **tenantId** - This is a unique string that identifies the tenant.
2. **key** - This is the key to use in retrieval of the effective configuration.

A sample get request for the effective config is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/effectiveconfig/{key}

This will return an EffectiveTenantConfigValue object, this object has two properties, the first is the value, this is the value of the configuration requested. The second is the value type, the value type is an enum that can either be CUSTOM for tenant specific configuration values or DEFAULT used when the tenant has no custom value set for this configuration so the default was obtained from the global config and return.


### Bulk effective config retrieval

A call can be made to retrieve all configuration that is available to a specific tenant. This can be configuration specified in global configuration that is used for all tenants or the custom configurations set for the teanatId specified in the request or a mix of both.

Bulk retrieval of an effective configuration value for a tenant requires the following information:

1. **tenantId** - This is a unique string that identifies the tenant.

A sample bulk get request for effective configs is as follows:

    http://<host>:<port>/data-processing-service/v1/tenants/{tenantId}/effectiveconfig

This will return an EffectiveTenantConfigs object that contains an array of tenant EffectiveTenantConfigValue objects. Each item in the array will specify if their value is from the tenants custom configuration or if it is from the default specified under the global configurations. 

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
