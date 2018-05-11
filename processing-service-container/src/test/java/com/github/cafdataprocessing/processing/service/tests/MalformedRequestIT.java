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
import com.github.cafdataprocessing.processing.service.tests.utils.EnvironmentPropertyProvider;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MalformedRequestIT
{
    @Test(description = "This test verifies that a malformed request body generates a 400 bad request error")
    public void putMalformedRequest() throws ApiException, IOException
    {
        try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            final HttpPut putRequest = new HttpPut(EnvironmentPropertyProvider.getWebServiceUrl() + "/globalConfig/ee.grammarMap");
            final StringEntity input = new StringEntity("{\"default\": \"test_value_01\", \"description\": \"test_description_01\"");
            input.setContentType("application/json");
            putRequest.setEntity(input);
            final HttpResponse response = client.execute(putRequest);
            Assert.assertTrue(response.getStatusLine().getStatusCode() == 400);
            Assert.assertTrue(response.getStatusLine().getReasonPhrase().equals("Bad Request"));
        }
    }
}
