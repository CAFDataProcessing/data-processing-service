# data processing service
Data Processing is a collection of CAF Microservices to enable analysis, extraction and transformation of data in an elastic scalable fashion.

## Overview

CAF Data Processing is a suite of components that facilitate the extraction, analysis and modification of data.
The wide variety of data sources available today makes data analytics and operations on data a challenging problem. Data Processing provides a flexible, elastic approach to solve this by allowing specialized services to be developed and included in a set of processing actions to be conditionally performed against the data.

The services are available as Docker containers to facilitate faster and simpler deployment. Consumers of Data Processing can elect to only include those services applicable to their needs, allowing for a minimum footprint without unnecessary resource allocation. Each application can also be managed separately with additional instances being spun up to meet higher workload demands or scaled back until required.

## Documentation
Documentation for data processing is available [here](http://cafapi.github.io)

## Feature Testing
The testing for the Data Processing Service is defined [here](testcases)

## Repository Contents

This GitHub repository contains the following projects.

### docs

Data Processing documentation.

### processing-service-core

The Data Processing Service web application.

### processing-service-client

An example of generating a Java client to contact the web application using the data-processing-service swagger contract.

### processing-service-container

Packages the Data Processing Service web application into a Docker container.

### processing-service-contract

Packages the swagger contract from the application into an artifact that can be specified as a Maven dependency in other projects.

### processing-service-example-requests

Example API method definitions for Postman that can be used to exercise the API.

### processing-service-ui

Builds the branded swagger UI with the data-processing-service swagger contract and copies it to the application.

### testcases

QA testcases

### utils

A collection of utility projects. e.g. examples projects that submit and receive tasks, pre-built data processing databases container for testing purposes.