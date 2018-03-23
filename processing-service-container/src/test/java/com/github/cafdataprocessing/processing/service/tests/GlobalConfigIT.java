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

import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.GlobalConfigApi;
import com.github.cafdataprocessing.processing.service.client.model.GlobalConfig;
import com.github.cafdataprocessing.processing.service.client.model.GlobalConfigs;
import com.github.cafdataprocessing.processing.service.client.model.GlobalConfigsEntry;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import org.junit.After;
import static org.junit.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GlobalConfigIT
{
    private static final String TEST_KEY_PREFIX = "test_key_";
    private static final String TEST_KEY_1 = TEST_KEY_PREFIX + "1";
    private static final String TEST_DESCRIPTION_PREFIX = "test_description_";
    private static final String TEST_DESCRIPTION_1 = TEST_DESCRIPTION_PREFIX + "1";
    private static final String TEST_VALUE_PREFIX = "test_value_";
    private static final String TEST_VALUE_1 = TEST_VALUE_PREFIX + "1";
    private static final String TEST_NOT_EXISTING_KEY = "test_not_existing_key";
    
    private static GlobalConfigApi globalConfigAPI;
    
    @BeforeClass
    public static void setupClass() throws Exception 
    {
        final ApiClient apiClient = ApiClientProvider.getApiClient();
        globalConfigAPI = new GlobalConfigApi(apiClient);
    }
    
    @After
    public void cleanUp() throws ApiException 
    {
        //delete all records after tests
        for (GlobalConfigsEntry config : globalConfigAPI.getGlobalConfigs()) {
            globalConfigAPI.deleteGlobalConfig(config.getKey());
        }
    }
    
    @Test
    public void createAndGetGlobalConfig() throws ApiException 
    {
        createConfigInStore("1");
        
        final GlobalConfig expectedConfig = buildGlobalConfig(TEST_VALUE_1, TEST_DESCRIPTION_1);
        final GlobalConfig actualConfig = globalConfigAPI.getGlobalConfig(TEST_KEY_1);
        assertEquals(actualConfig, expectedConfig);
    }
    
    @Test
    public void createGlobalConfigWithKeyLongerThan255() throws ApiException 
    {
        
        try {
            createConfigInStore("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch (ApiException e) {
            assertTrue(e.getCode() == 400);
        }
        
        final GlobalConfig expectedConfig = buildGlobalConfig(TEST_VALUE_1, TEST_DESCRIPTION_1);
        final GlobalConfig actualConfig = globalConfigAPI.getGlobalConfig(TEST_KEY_1);
        assertEquals(actualConfig, expectedConfig);
    }
    
    @Test
    public void updateGlobalConfig() throws ApiException 
    {
        createConfigInStore("1");
        
        final GlobalConfig expectedConfig = buildGlobalConfig(TEST_VALUE_1, TEST_DESCRIPTION_1);
        GlobalConfig actualConfig = globalConfigAPI.getGlobalConfig(TEST_KEY_1);
        assertEquals(actualConfig, expectedConfig);
        
        // update only the value
        final String updatedValue = "updated_" + TEST_DESCRIPTION_1;
        
        globalConfigAPI.setGlobalConfig(TEST_KEY_1, buildGlobalConfig(updatedValue, TEST_DESCRIPTION_1));
        
        expectedConfig.setDefault(updatedValue);
        actualConfig = globalConfigAPI.getGlobalConfig(TEST_KEY_1);
        assertEquals(actualConfig, expectedConfig);
    }
    
    @Test
    public void getGlobalConfigOnNotExistingRecord() throws ApiException 
    {
        try {
            globalConfigAPI.getGlobalConfig(TEST_NOT_EXISTING_KEY);
        } catch (ApiException e) {
            assertTrue(e.getCode() == 404);
        }
    }
    
    @Test
    public void getAllGlobalConfigsForExistigRecords() throws ApiException 
    {
        createConfigInStore("1");
        createConfigInStore("2");
        
        GlobalConfigs expectedConfigs = new GlobalConfigs();
        expectedConfigs.add(buildGlobalConfigEntryWithID("1"));
        expectedConfigs.add(buildGlobalConfigEntryWithID("2"));
        
        GlobalConfigs actualConfigs = globalConfigAPI.getGlobalConfigs();        
        assertTrue(actualConfigs.equals(expectedConfigs));
    }
    
    @Test(description = "Test that an empty array is returned and not a null.")
    public void getAllGlobalConfigsForEmptyRecords() throws ApiException 
    {
        assertTrue(globalConfigAPI.getGlobalConfigs().isEmpty());
    }
    
    @Test
    public void deleteGlobalConfigOnExistingRecord() throws ApiException 
    {
        createConfigInStore("1");
        globalConfigAPI.deleteGlobalConfig(TEST_KEY_1);
        
        final GlobalConfigsEntry deletedConfig = new GlobalConfigsEntry();
        deletedConfig.setKey(TEST_KEY_1);
        deletedConfig.setDefault(TEST_VALUE_1);
        deletedConfig.setDescription(TEST_DESCRIPTION_1);
        
        assertTrue(!globalConfigAPI.getGlobalConfigs().contains(deletedConfig));
    }
    
    @Test
    public void deleteGlobalConfigOnNotExistingRecord() 
    {
        try {
            globalConfigAPI.deleteGlobalConfig(TEST_NOT_EXISTING_KEY);
        } catch (ApiException e) {
            assertTrue(e.getCode() == 404);
        }
    }
    
    private void createConfigInStore(final String id) throws ApiException
    {
        final String testKey = TEST_KEY_PREFIX + id;
        globalConfigAPI.setGlobalConfig(testKey, buildGlobalConfig(TEST_VALUE_PREFIX + id, TEST_DESCRIPTION_PREFIX + id));
    }
    
    private GlobalConfig buildGlobalConfig(String value, String descrtiption)
    {
        final GlobalConfig config = new GlobalConfig();
        config.setDefault(value);
        config.setDescription(descrtiption);
        return config;
    }
    
    private GlobalConfigsEntry buildGlobalConfigEntryWithID(final String id)
    {
        final GlobalConfigsEntry configsEntry = new GlobalConfigsEntry();
        configsEntry.setKey(TEST_KEY_PREFIX + id);
        configsEntry.setDefault(TEST_VALUE_PREFIX + id);
        configsEntry.setDescription(TEST_DESCRIPTION_PREFIX + id);
        
        return configsEntry;
    }
}
