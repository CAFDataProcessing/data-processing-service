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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.client.model.*;
import org.testng.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;


/**
 * Performs comparison on the 'additional' property of conditions and classifications.
 */
public class AdditionalPropertyComparison {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void compareAdditional(ConditionCommon.TypeEnum expectedType, ConditionCommon.TypeEnum retrievedType,
                                   Object expectedAdditional, Object retrievedAdditional){
        switch(retrievedType){
            case BOOLEAN:
                if(expectedType.equals(ConditionCommon.TypeEnum.BOOLEAN)){
                    compareBooleanConditionAdditionals(
                            mapper.convertValue(expectedAdditional, BooleanConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, BooleanConditionAdditional.class));
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is BOOLEAN.");
                }
                break;
            case STRING:
                if(expectedType.equals(ConditionCommon.TypeEnum.STRING)) {
                    compareStringConditionAdditionals(
                            mapper.convertValue(expectedAdditional, StringConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, StringConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is STRING.");
                }
                break;
            case NUMBER:
                if(expectedType.equals(ConditionCommon.TypeEnum.NUMBER)) {
                    compareNumberConditionAdditionals(
                            mapper.convertValue(expectedAdditional, NumberConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, NumberConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is NUMBER.");
                }
                break;
            case REGEX:
                if(expectedType.equals(ConditionCommon.TypeEnum.REGEX)) {
                    compareRegexConditionAdditionals(
                            mapper.convertValue(expectedAdditional, RegexConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, RegexConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is REGEX.");
                }
                break;
            case DATE:
                if(expectedType.equals(ConditionCommon.TypeEnum.DATE)) {
                    compareDateConditionAdditionals(
                            mapper.convertValue(expectedAdditional, DateConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, DateConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is DATE.");
                }
                break;
            case NOT:
                if(expectedType.equals(ConditionCommon.TypeEnum.NOT)) {
                    compareNotConditionAdditionals(
                            mapper.convertValue(expectedAdditional, NotConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, NotConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is NOT.");
                }
                break;
            case EXISTS:
                if(expectedType.equals(ConditionCommon.TypeEnum.EXISTS)) {
                    compareExistsConditionAdditionals(
                            mapper.convertValue(expectedAdditional, ExistsConditionAdditional.class),
                            mapper.convertValue(retrievedAdditional, ExistsConditionAdditional.class)
                    );
                }
                else {
                    Assert.fail("Expected and retrieved additional properties are not instances of the same type. Retrieved is EXISTS.");
                }
                break;
            default:
                Assert.fail("Unrecognized type returned on Classification additional.");
        }
    }

    public static void compareBooleanConditionAdditionals(BooleanConditionAdditional expectedAdditional,
                                                    BooleanConditionAdditional retrievedAdditional){
        //TODO bug in classifications, notes should not be exposed as a field except on children of a boolean condition used in classification.
        //Assert.assertEquals(retrievedAdditional.getNotes(), expectedAdditional.getNotes(), "Expecting same notes value on Boolean Condition.");
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(), "Expecting same order value on Boolean Condition");
        Assert.assertEquals(retrievedAdditional.getOperator(), expectedAdditional.getOperator(),
                "Expecting same operator on Boolean condition.");
        List<Condition> retrievedChildConditions = retrievedAdditional.getChildren();
        List<Condition> expectedChildConditions = expectedAdditional.getChildren();

        Assert.assertEquals(retrievedChildConditions.size(), expectedChildConditions.size(),
                "Number of child conditions on the Boolean should be as expected.");

        for(Condition retrievedChildCondition: retrievedChildConditions){
            Optional<Condition> foundConditionOptional = expectedChildConditions.stream()
                    .filter(condition -> condition.getName().equals(retrievedChildCondition.getName())).findFirst();
            if(!foundConditionOptional.isPresent()){
                Assert.fail("Boolean Condition returned is missing an expected child.");
            }
            Condition foundCondition = foundConditionOptional.get();
            ConditionCommon retrievedChildConditionAdditional = mapper.convertValue(retrievedChildCondition.getAdditional(),
                    ConditionCommon.class);
            ConditionCommon foundConditionAdditionalAsConditionCommon = mapper.convertValue(foundCondition.getAdditional(),
                    ConditionCommon.class);
            //compare the additional properties on the matched condition
            compareAdditional(foundConditionAdditionalAsConditionCommon.getType(), retrievedChildConditionAdditional.getType(),
                    foundCondition.getAdditional(), retrievedChildCondition.getAdditional());
        }
    }

    public static void compareNumberConditionAdditionals(NumberConditionAdditional expectedAdditional,
                                                   NumberConditionAdditional retrievedAdditional){
        //TODO currently a bug in classifications where notes field won't be persisted.
        //Assert.assertEquals(retrievedAdditional.getNotes(), expectedAdditional.getNotes(),
        //        "Note on Number condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on Number condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getOperator(), expectedAdditional.getOperator(),
                "Operator on Number condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getField(), expectedAdditional.getField(),
                "Field on Number condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getValue(), expectedAdditional.getValue(),
                "Value on Number condition should be expected value.");
    }

    public static void compareStringConditionAdditionals(StringConditionAdditional expectedAdditional,
                                                   StringConditionAdditional retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.getNotes(), expectedAdditional.getNotes(),
                "Note on String condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on String condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getOperator(), expectedAdditional.getOperator(),
                "Operator on String condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getField(), expectedAdditional.getField(),
                "Field on String condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getValue(), expectedAdditional.getValue(),
                "Value on String condition should be expected value.");
    }

    public static void compareRegexConditionAdditionals(RegexConditionAdditional expectedAdditional,
                                                  RegexConditionAdditional retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on Regex condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getValue(), expectedAdditional.getValue(),
                "Value on Regex condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getField(), expectedAdditional.getField(),
                "Field on Regex condition should be expected value.");
    }

    public static void compareDateConditionAdditionals(DateConditionAdditional expectedAdditional,
                                                 DateConditionAdditional retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on Date condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getValue(), expectedAdditional.getValue(),
                "Value on Date condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getField(), expectedAdditional.getField(),
                "Field on Date condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getOperator(), expectedAdditional.getOperator(),
                "Operator on Date condition should be expected value.");
    }

    public static void compareNotConditionAdditionals(NotConditionAdditional expectedAdditional,
                                                NotConditionAdditional retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on Not condition should be expected value.");
        Condition expectedCondition = (Condition) expectedAdditional.getCondition();
        Condition retrievedCondition = mapper.convertValue(retrievedAdditional.getCondition(), Condition.class);

        Assert.assertEquals(retrievedCondition.getName(), expectedCondition.getName(),
                "Name on the condition under Not condition should be as expected.");

        ConditionCommon retrievedConditionAdditional = mapper.convertValue(retrievedCondition.getAdditional(),
                ConditionCommon.class);
        ConditionCommon expectedConditionAdditional = mapper.convertValue(expectedCondition.getAdditional(),
                ConditionCommon.class);

        compareAdditional(expectedConditionAdditional.getType(),
                retrievedConditionAdditional.getType(),
                expectedCondition.getAdditional(),
                retrievedCondition.getAdditional());
    }

    public static void compareExistsConditionAdditionals(ExistsConditionAdditional expectedAdditional,
                                                   ExistsConditionAdditional retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.getOrder(), expectedAdditional.getOrder(),
                "Order on Exists condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.getField(), expectedAdditional.getField(),
                "Field on Exists condition should be expected value.");
    }

    public static void compareTextConditionAdditionals(LinkedHashMap<String, Object> expectedAdditional,
                                                 LinkedHashMap<String, Object> retrievedAdditional){
        Assert.assertEquals(retrievedAdditional.get("field"), expectedAdditional.get("field"),
                "Field on Text condition should be expected value.");
        Assert.assertEquals(retrievedAdditional.get("value"), expectedAdditional.get("value"),
                "Value on Text condition should be expected value.");
    }
}
