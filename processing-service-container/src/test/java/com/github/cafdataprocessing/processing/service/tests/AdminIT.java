/*
 * Copyright 2017-2018 Micro Focus or one of its affiliates.
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

import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.AdminApi;
import com.github.cafdataprocessing.processing.service.client.model.HealthStatus;
import com.github.cafdataprocessing.processing.service.client.model.HealthStatusDependencies;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Integration tests for the functionality of the Processing Service.
 */
public class AdminIT {
    private static AdminApi adminApi;

    @BeforeClass
    public static void setup() throws Exception {
        adminApi = new AdminApi(ApiClientProvider.getApiClient());
    }

    /**
     * Tests that the health check for Processing API reports as healthy and that the state of dependant components are also reported as healthy.
     */
    @Test
    public void testHealthCheck() throws ApiException {
        HealthStatus health = adminApi.healthCheck();
        Assert.assertEquals(health.getStatus(), HealthStatus.StatusEnum.HEALTHY, "Status should be HEALTHY.");
        for(HealthStatusDependencies dependency : health.getDependencies()){
            Assert.assertEquals(dependency.getStatus(), HealthStatusDependencies.StatusEnum.HEALTHY,
                    "Dependency "+ dependency.getName() +" should be HEALTHY");
        }
    }
    
    @Test
    public void testHealthCheckEndpoint() throws IOException
    {
        final String getRequestUrl = System.getenv("PROCESSING_SERVICE_HEALTHCHECK_URL");
        final HttpGet request = new HttpGet(getRequestUrl);
        final HttpClient httpClient = HttpClients.createDefault();
        final HttpResponse response = httpClient.execute(request);
        request.releaseConnection();
        if (response.getEntity() == null) {
            fail("There was no content returned from the HealthCheck HTTP Get Request");
        }
        
        final String expectedHealthCheckResponseContent
            = "{\"status\":\"HEALTHY\",\"dependencies\":[{\"name\":\"PROCESSING_DATABASE\",\"status\":\"HEALTHY\"},"
            + "{\"name\":\"POLICY_API\",\"status\":\"HEALTHY\"}]}";
        assertEquals(expectedHealthCheckResponseContent, IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));

        assertTrue(response.getStatusLine().getStatusCode() == 200);
    }
}
