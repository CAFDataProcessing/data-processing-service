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

import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.GlobalConfigurationApi;
import com.github.cafdataprocessing.processing.service.client.model.GlobalConfigsEntry;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import static org.junit.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoggingIT
{
    private static GlobalConfigurationApi globalConfigurationApi;
    private static final String KEY_WITH_NEW_LINE_CHARACTERS = "\ntest_not_\n_existing_key\n";
    
    @BeforeClass
    public static void setupClass() throws Exception 
    {
        final ApiClient apiClient = ApiClientProvider.getApiClient();
        globalConfigurationApi = new GlobalConfigurationApi(apiClient);
    }
    
    @AfterMethod
    public void cleanUp() throws ApiException 
    {
        //delete all records after tests
        for (GlobalConfigsEntry config : globalConfigurationApi.getGlobalConfigs()) {
            globalConfigurationApi.deleteGlobalConfig(config.getKey());
        }
    }
    
    @Test(description = "Requires to be checked manualy in logs, that the line is not broken into multiple lines.")
    public void testLogKeyWithNewLineCharacter() throws ApiException 
    {
        try {
            globalConfigurationApi.getGlobalConfig(KEY_WITH_NEW_LINE_CHARACTERS);
        } catch (ApiException e) {
            assertTrue(e.getCode() == 404);
        }
    }
}
