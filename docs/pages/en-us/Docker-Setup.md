---
layout: default
title: Docker Setup

banner:
    icon: 'assets/img/data-processing-graphic.png'
    title: Data Processing Service
    subtitle: Analyze a Larger Range of Formats
    links:
        - title: GitHub
          url: https://github.com/CAFDataProcessing/data-processing-service
---

# Docker Setup

This page demonstrates configuring a Docker for Windows install to be able to launch the containers using the data-processing-service-deploy compose file.

## Shared Drive

Set the drives that you want to be available to the containers e.g. those containing license files.

![Shared Drive](../../assets/img/Docker_Setup/1_shared.png)

## Proxies

Configure the proxies used to pull images to use the HPE proxies.

![Set up Proxies](../../assets/img/Docker_Setup/2_proxy.png)
