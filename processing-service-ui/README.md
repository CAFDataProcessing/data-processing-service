# processing-service-ui

This Swagger UI project builds an API documentation page for the [Data Processing Service](../processing-service-core). It allows users to see what operations the web service exposes as well as documentation relating to those operations and the web service in general. The page can also be used to issue requests to the service.

## Usage

Running the Maven 'package' phase will retrieve the contract, create the UI page for it and copy that page to the processing-service-core NodeJS application in the appropriate location.