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

import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.AdminApi;
import com.github.cafdataprocessing.processing.service.client.model.HealthStatus;
import com.github.cafdataprocessing.processing.service.client.model.HealthStatusDependencies;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import org.testng.Assert;
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
}
