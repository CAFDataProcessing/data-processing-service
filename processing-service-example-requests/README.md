# processing-service-example-requests

A collection of Postman requests for the Data Processing Service. Operations available on the API are be represented in this collection. Postman is a REST client that allows developers to interface with REST APIs.

## Setup

Navigate to the Postman website. Download and launch the chrome app. Note you may be asked to sign up upon your first time, you can **skip** this and continue using the application.

`https://www.getpostman.com/`

Next, copy the processing_postman_examples.json file in this repository to a location on your disk.

Navigate to *examples > gateway_postman_examples.json* and open the JSON in a text editor that can perform a **"find and replace"** (Notepad++ is able).

Replace your local address (*http://127.0.0.1:8080*) with the supplied host and port that the container uses for each of the requests.


Save your changes in the JSON and open up Postman. Click on **Import**
at the top left corner of the screen. This opens a smaller window where you can drag or choose files to be imported. Drag *gateway_postman_examples.json* into the window or click on **Choose files** and import it that way.


Once imported the JSON will be separated into different groups in the navigation bar to the left (ActionTypes, Rules, Workflow, etc). Open up a few folders to take a look at the different actions that were imported.

Underneath the groups there is a single item called **HealthCheck**. This is the same health check request previously run in your browser. Click on HealthCheck to update the screen.

In the address bar within Postman the HealthCheck GET request will appear, Send the request by clicking on the blue **Send** button beside the address. After a brief moment the request will return the results of the health check, similar to what was run in your browser earlier.

You can run other methods through Postman to test the data-processing-service-container, you can also add Parameters by clicking on **Params** beside the Send button and entering the required parameters into the **Key=Value** fields before sending the request.

Further information on using Postman can be found [here](https://www.getpostman.com/docs/).