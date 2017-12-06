/*
 * Copyright 2015-2017 EntIT Software LLC, a Micro Focus company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cafdataprocessing.processing.service.tests;

import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.EnvironmentPropertyProvider;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Integration tests for Swagger related API paths in the Data Processing Service.
 */
public class SwaggerIT {
    private static ApiClient apiClient;

    @BeforeClass
    public static void setup() throws Exception {
        apiClient = ApiClientProvider.getApiClient();
    }

    @Test(description = "Retrives the swagger contract from the data processing service.")
     public void getContract() throws IOException {
        URL swaggerContractUrl = new URL(EnvironmentPropertyProvider.getWebServiceUrl()+"/swagger.yaml");
        HttpURLConnection swaggerContractConnection = (HttpURLConnection) swaggerContractUrl.openConnection();
        int responseCode = swaggerContractConnection.getResponseCode();
        Assert.assertEquals(responseCode, 200, "Expecting a successful response code of 200 from swagger contract request.");
        StringBuffer responseAsStringBuffer = readResponse(swaggerContractConnection.getInputStream());
        String contractAsString = responseAsStringBuffer.toString();
        Assert.assertTrue(contractAsString.contains("swagger: \"2.0\""), "Response should have the swagger contract heading.");
        Assert.assertTrue(contractAsString.contains("title: \"Data Processing Service API\""), "Response should have the data processing service title.");
    }

    @Test(description = "Retrives the swagger UI from the data processing service.")
    public void getUI() throws IOException {
        String uiUrlPath = EnvironmentPropertyProvider.getHost()+"/data-processing-ui";

        URL swaggerUiUrl = new URL(uiUrlPath);
        HttpURLConnection swaggerUiConnection = (HttpURLConnection) swaggerUiUrl.openConnection();
        int responseCode = swaggerUiConnection.getResponseCode();
        Assert.assertEquals(responseCode, 200, "Expecting a successful response code of 200 from swagger UI request.");
        StringBuffer responseAsStringBuffer = readResponse(swaggerUiConnection.getInputStream());
        String uiAsString = responseAsStringBuffer.toString();
        Assert.assertTrue(uiAsString.contains("<!DOCTYPE html><html><head>"), "Response should be an HTML format document.");
        Assert.assertTrue(uiAsString.contains("<title>CAF Service API</title>"), "Response should be the UI for the data processing service.");
    }

    private StringBuffer readResponse(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }
}
