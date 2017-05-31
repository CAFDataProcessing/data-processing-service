/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
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
package com.github.cafdataprocessing.processing.service.tests.utils;

import org.testng.Assert;

import java.util.*;

/**
 * An example of an action type definition (that represents a JSON schema definition) to be used when creating an action type and as a reference for settings on
 * an action.
 */
public class ExampleActionTypeDefinition {
    private String description;
    private String title;
    private String type;
    private LinkedHashMap<String, Object> properties;

    /**
     * Default constructor will set the properties of the object.
     */
    public ExampleActionTypeDefinition(){
        description = "desc_"+ UUID.randomUUID().toString();
        title = "title_"+UUID.randomUUID().toString();
        type = "object";
        properties = new LinkedHashMap<>();
        //defining some default properties
        LinkedHashMap<String, String> subProperty_1 = new LinkedHashMap<>();
        subProperty_1.put("type", "string");
        subProperty_1.put("description", "subDesc_"+UUID.randomUUID().toString());

        properties.put("prop_"+UUID.randomUUID().toString(), subProperty_1);

        LinkedHashMap<String, String> subProperty_2 = new LinkedHashMap<>();
        subProperty_2.put("type", "string");
        subProperty_2.put("description", "subDesc_"+UUID.randomUUID().toString());
        subProperty_2.put("default", "default_"+UUID.randomUUID().toString());

        properties.put("prop_"+UUID.randomUUID().toString(), subProperty_2);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedHashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(LinkedHashMap<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Creates valid settings object for an Action that is using this type.
     * @return
     */
    public Object generateSettingsForType(){
        LinkedHashMap<String, Object> settings = new LinkedHashMap<>();
        for(Map.Entry<String, Object> entry : getProperties().entrySet()){
            settings.put(entry.getKey(), "value_"+UUID.randomUUID().toString());
        }
        return settings;
    }

    public static void compareProperties(LinkedHashMap<String, Object> expectedProperties,
                                       LinkedHashMap<String, Object> retrievedProperties){
        List<String> expectedKeys = new LinkedList<>();
        expectedKeys.addAll(expectedProperties.keySet());

        for (Map.Entry<String, Object> retrievedProperty : retrievedProperties.entrySet()) {
            //find matching key in expected properties
            Optional<String> foundKey = expectedKeys.stream().filter(exKey -> exKey.equals(retrievedProperty.getKey())).findFirst();
            if(!foundKey.isPresent()){
                Assert.fail("Unable to find returned key in expected list of properties on type definition. Key: " + retrievedProperty.getKey());
            }
            //remove this key from list of keys to find in future loops
            expectedKeys.remove(foundKey.get());

            Object foundValue = expectedProperties.get(foundKey.get());
            Object retrievedValue = retrievedProperties.get(foundKey.get());
            if(retrievedValue instanceof String){
                if(foundValue instanceof String){
                    Assert.assertEquals((String)retrievedValue, (String) foundValue,
                            "Expecting values on definition property to be the same. Key was: "+foundKey.get());
                }
                else {
                    Assert.fail("Retrieved value on definition property is a String but this is not expected type.");
                }
            }
            else {
                //example type definition is either String or LinkedHashMap
                compareProperties((LinkedHashMap) foundValue, (LinkedHashMap) retrievedValue);
            }
        }
        Assert.assertTrue(expectedKeys.isEmpty(),
                "Should have found all expected property names on returned set of properties in action type definition.");
    }
}
