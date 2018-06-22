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
package com.github.cafdataprocessing.processing.service.tests.utils;

import com.github.cafdataprocessing.processing.service.client.model.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.cafdataprocessing.processing.service.client.model.ConditionCommon.TypeEnum.*;

/**
 * Contains methods to return initialized objects for use in API.
 */
public class ObjectsInitializer {
    /**
     * Creates a BaseWorkflow and populates its properties with random values.
     * @return The constructed BaseWorkflow object.
     */
    public static BaseWorkflow initializeWorkflow(){
        BaseWorkflow workflow = new BaseWorkflow();
        workflow.setName("name_"+UUID.randomUUID().toString());
        workflow.setDescription("description_"+UUID.randomUUID().toString());
        workflow.setNotes("notes_"+UUID.randomUUID().toString());
        return workflow;
    }

    public static BaseProcessingRule initializeProcessingRule(Integer priority){
        BaseProcessingRule processingRule = new BaseProcessingRule();
        processingRule.setName("name_"+UUID.randomUUID().toString());
        processingRule.setDescription("description_" + UUID.randomUUID().toString());
        processingRule.setPriority(priority);
        processingRule.setEnabled(true);
        return processingRule;
    }

    public static ActionType initializeActionType(Object definition){
        ActionType actionType = new ActionType();
        actionType.setName("name_"+UUID.randomUUID().toString());
        actionType.setDescription("description_"+UUID.randomUUID().toString());
        actionType.setInternalName("internal_"+UUID.randomUUID().toString());
        if(definition!=null){
            actionType.setDefinition(definition);
        }
        else{
            actionType.setDefinition(new ExampleActionTypeDefinition());
        }
        return actionType;
    }

    public static Action initializeAction(Integer order, long typeId, Object settings){
        Action action = new Action();
        action.setName("name_"+UUID.randomUUID().toString());
        action.setDescription("description_"+UUID.randomUUID().toString());
        action.setOrder(order);
        action.setTypeId(typeId);
        action.setSettings(settings);
        return action;
    }

    /**
     * Creates a StringConditionAdditional object and populates its value, type and field properties.
     * The order and operator properties are defaulted to 'STARTS_WITH' and 100.
     * @return Constructed StringConditionAdditional object.
     */
    public static StringConditionAdditional initializeStringConditionAdditional(){
        StringConditionAdditional stringConditionAdditional = new StringConditionAdditional();
        stringConditionAdditional.setOperator(StringConditionAdditional.OperatorEnum.STARTS_WITH);
        stringConditionAdditional.setValue("value_"+UUID.randomUUID().toString());
        stringConditionAdditional.setOrder(100);
        stringConditionAdditional.setType(ConditionCommon.TypeEnum.STRING);
        stringConditionAdditional.setField("field+"+UUID.randomUUID().toString());
        return stringConditionAdditional;
    }

    /**
     * Creates a NumberConditionAdditional object with a random value. Sets type and field properties.
     * The order and operator properties are defaulted to 'EQ' and 100.
     * @return Constructed NumberConditionAdditional object.
     */
    public static NumberConditionAdditional initializeNumberConditionAdditional(Long value){
        NumberConditionAdditional numberConditionAdditional = new NumberConditionAdditional();
        numberConditionAdditional.setValue(value);
        numberConditionAdditional.setOperator(NumberConditionAdditional.OperatorEnum.EQ);
        numberConditionAdditional.setOrder(100);
        numberConditionAdditional.setField("field_"+UUID.randomUUID().toString());
        numberConditionAdditional.setType(ConditionCommon.TypeEnum.NUMBER);
        return numberConditionAdditional;
    }

    /**
     * Creates a NumberConditionAdditional object with a random value. Sets type and field properties.
     * The order and operator properties are defaulted to 'EQ' and 100.
     * @return Constructed NumberConditionAdditional object.
     */
    public static NumberConditionAdditional initializeNumberConditionAdditional(){
        return initializeNumberConditionAdditional(ThreadLocalRandom.current().nextLong());
    }

    /**
     * Creates a RegexConditionAdditional object and populates its value, type and field properties.
     * The order property is defaulted to 100.
     * @return Constructed RegexConditionAdditional object.
     */
    public static RegexConditionAdditional initializeRegexConditionAdditional(){
        RegexConditionAdditional regexConditionAdditional = new RegexConditionAdditional();
        regexConditionAdditional.setOrder(100);
        regexConditionAdditional.setType(ConditionCommon.TypeEnum.REGEX);
        regexConditionAdditional.setField("field_"+UUID.randomUUID().toString());
        regexConditionAdditional.setValue("value_"+UUID.randomUUID().toString());
        return regexConditionAdditional;
    }

    /**
     * Creates a DateConditionAdditional object and populates its value, type and field properties.
     * The order and operator properties are defaulted to 100 and AFTER.
     * @return Constructed DateConditionAdditional object.
     */
    public static DateConditionAdditional initializeDateConditionAdditional(){
        DateConditionAdditional dateConditionAdditional = new DateConditionAdditional();
        dateConditionAdditional.setOrder(100);
        dateConditionAdditional.setType(ConditionCommon.TypeEnum.DATE);
        dateConditionAdditional.setField("field_"+UUID.randomUUID().toString());
        dateConditionAdditional.setOperator(DateConditionAdditional.OperatorEnum.AFTER);
        Random rnd = new Random();
        Date dateValue = new Date(Math.abs(System.currentTimeMillis() - rnd.nextLong()));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateConditionAdditional.setValue(dateFormatter.format(dateValue));
        return dateConditionAdditional;
    }

    public static ExistsConditionAdditional initializeExistsConditionAdditional(){
        ExistsConditionAdditional existsConditionAdditional = new ExistsConditionAdditional();
        existsConditionAdditional.setOrder(100);
        existsConditionAdditional.setField("field_"+UUID.randomUUID().toString());
        existsConditionAdditional.setType(ConditionCommon.TypeEnum.EXISTS);
        return existsConditionAdditional;
    }

    /**
     * Creates a NotConditionAdditional object and populates its type property. Order is defaulted to 100.
     * The condition property will even default to a Condition with StringConditionAdditional or if a condition provided in params
     * then that will be used.
     * @param conditionToNegate Optional condition to set on the NotConditionAdditional object. Pass null to default to a
     *                          StringConditionAdditional.
     * @return Constructed NotConditionAdditional
     */
    public static NotConditionAdditional initializeNotConditionAdditional(Condition conditionToNegate){
        NotConditionAdditional notConditionAdditional = new NotConditionAdditional();
        notConditionAdditional.setOrder(100);
        notConditionAdditional.setType(ConditionCommon.TypeEnum.NOT);
        if(conditionToNegate!=null) {
            notConditionAdditional.setCondition(conditionToNegate);
        }
        else {
            Condition newCondition = initializeCondition(null);
            notConditionAdditional.setCondition(newCondition);
        }
        return notConditionAdditional;
    }

    /**
     * Creates a Condition object and populates its name property. The additional property will be set to either the passed in
     * additional param if it is not null or to a StringConditionAdditional.
     * @param additional
     * @return Constructed Condition object.
     */
    public static Condition initializeCondition(Object additional){
        Condition newCondition = new Condition();
        newCondition.setName("name_"+UUID.randomUUID().toString());
        if(additional!=null){
            newCondition.setAdditional(additional);
        }
        else{
            StringConditionAdditional stringConditionAdditional = initializeStringConditionAdditional();
            newCondition.setAdditional(stringConditionAdditional);
        }
        return newCondition;
    }

    /**
     * * Creates a BooleanConditionAdditional object and populates its properties.
     * The order and operator properties are defaulted to 100 and AFTER.
     * @param childConditions Child conditions to set on the BooleanConditionAdditional. If null then a default of a string and a
     *                        number condition are added as children.
     * @returnConstructed BooleanConditionAdditional object.
     */
    public static BooleanConditionAdditional initializeBooleanConditionAdditional(List<Condition> childConditions){
        BooleanConditionAdditional booleanConditionAdditional = new BooleanConditionAdditional();
        booleanConditionAdditional.setNotes(UUID.randomUUID().toString());
        booleanConditionAdditional.setOrder(1);
        booleanConditionAdditional.setOperator(BooleanConditionAdditional.OperatorEnum.AND);
        booleanConditionAdditional.setType(BOOLEAN);

        StringConditionAdditional booleanChild_1_additional = new StringConditionAdditional();
        booleanChild_1_additional.setOperator(StringConditionAdditional.OperatorEnum.STARTS_WITH);
        booleanChild_1_additional.setValue(UUID.randomUUID().toString());
        booleanChild_1_additional.setField(UUID.randomUUID().toString());
        booleanChild_1_additional.setType(STRING);
        booleanChild_1_additional.setOrder(100);
        Condition booleanChild_1 = new Condition();
        booleanChild_1.setName(UUID.randomUUID().toString());
        booleanChild_1.setAdditional(booleanChild_1_additional);

        NumberConditionAdditional booleanChild_2_additional = new NumberConditionAdditional();
        booleanChild_2_additional.setField(UUID.randomUUID().toString());
        booleanChild_2_additional.setOperator(NumberConditionAdditional.OperatorEnum.EQ);
        //bug in conversion of long from java to javascript causing loss of precision so using Int here for now.
        booleanChild_2_additional.setValue((long) ThreadLocalRandom.current().nextInt());
        booleanChild_2_additional.setType(NUMBER);
        booleanChild_2_additional.setOrder(200);
        Condition booleanChild_2 = new Condition();
        booleanChild_2.setName(UUID.randomUUID().toString());
        booleanChild_2.setAdditional(booleanChild_2_additional);
        booleanConditionAdditional.setChildren(Arrays.asList(booleanChild_1, booleanChild_2));
        return booleanConditionAdditional;
    }
}
