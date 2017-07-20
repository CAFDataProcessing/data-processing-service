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
package com.github.cafdataprocessing.utilities.initialization;

import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ActionJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.MergeMode;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.ProcessingRuleJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.WorkflowJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions.ConditionAdditionalJson;
import com.github.cafdataprocessing.utilities.initialization.jsonobjects.conditions.ConditionJson;
import com.google.common.base.Strings;
import org.apache.commons.lang.NotImplementedException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Class responsible for merging of two workflow objects - a base one and other one with overrides and / or extensions.
 */
public class WorkflowCombiner
{
    /**
     * Combines (merges) two workflow objects.
     * Merging is based on the name attributes of subtypes. For example, if a {@link ProcessingRuleJson} has
     * a name "Metadata Processing" and the {@code overlayWorkflow} also has a {@link ProcessingRuleJson} with the same
     * name ("Metadata Processing"), properties of the processing rule in the overlay will be merged into the original
     * workflow. Merging will either extend the original workflow or will override values in the original object.
     * If a particular value is not set in the original workflow, it will be added. If it exists - it will be overridden.
     * There is an option to force complete replacement behaviour. {@link ProcessingRuleJson} and {@link ActionJson} have
     * {@link MergeMode} property. If this property is set to {@code REPLACE}, values in matched (by name) in
     * Processing Rule or Action will replace values in the {@code targetOriginalWorkflow}.
     * {@link MergeMode} value for both objects defaults to {@code MERGE}.
     *
     * @param targetOriginalWorkflow a base workflow to which apply the overrides and / or extensions
     * @param overlayWorkflow a workflow object with overrides and / or extensions
     */
    public static void combineWorkflows(WorkflowJson targetOriginalWorkflow, WorkflowJson overlayWorkflow)
    {
        Objects.requireNonNull(targetOriginalWorkflow);
        if (overlayWorkflow == null) {
            return;
        }
        for (ProcessingRuleJson overlayProcessingRule : overlayWorkflow.processingRules) {
            Optional<ProcessingRuleJson> baseWorkflowProcessingRule = targetOriginalWorkflow.processingRules.stream().filter(rule -> rule.name.equals(overlayProcessingRule.name)).findFirst();
            if (baseWorkflowProcessingRule.isPresent()) {
                combineProcessingRule(baseWorkflowProcessingRule.get(), overlayProcessingRule);
            }
            else {
                targetOriginalWorkflow.processingRules.add(overlayProcessingRule);
            }
        }
    }

    private static void combineProcessingRule(ProcessingRuleJson processingRule, ProcessingRuleJson overlayProcessingRule)
    {
        if (overlayProcessingRule.mergeMode == MergeMode.REPLACE) {
            processingRule.enabled = overlayProcessingRule.enabled;
            processingRule.priority = overlayProcessingRule.priority;
            processingRule.description = overlayProcessingRule.description;
            processingRule.actions = overlayProcessingRule.actions;
            processingRule.ruleConditions = overlayProcessingRule.ruleConditions;
        }
        if (overlayProcessingRule.enabled != null) {
            processingRule.enabled = overlayProcessingRule.enabled;
        }
        if (overlayProcessingRule.priority != null) {
            processingRule.priority = overlayProcessingRule.priority;
        }
        if (!Strings.isNullOrEmpty(overlayProcessingRule.description)) {
            processingRule.description = overlayProcessingRule.description;
        }
        if (overlayProcessingRule.actions != null) {
            for (ActionJson overlayAction : overlayProcessingRule.actions) {
                Optional<ActionJson> actionJson = processingRule.actions.stream().filter(action -> action.name.equals(overlayAction.name)).findFirst();
                if (actionJson.isPresent()) {
                    combineAction(actionJson.get(), overlayAction);
                }
                else {
                    processingRule.actions.add(overlayAction);
                }
            }
        }
        if (overlayProcessingRule.ruleConditions != null) {
            if (processingRule.ruleConditions == null) {
                processingRule.ruleConditions = overlayProcessingRule.ruleConditions;
            }
            else {
                for (ConditionJson overlayActionCondition : overlayProcessingRule.ruleConditions) {
                    Optional<ConditionJson> originalCondition = processingRule.ruleConditions.stream().filter(condition -> condition.name.equals(overlayActionCondition.name)).findFirst();
                    if (originalCondition.isPresent()) {
                        combineCondition(originalCondition.get(), overlayActionCondition);
                    }
                    else {
                        processingRule.ruleConditions.add(overlayActionCondition);
                    }
                }
            }
        }
    }

    private static void combineAction(ActionJson action, ActionJson overlayAction)
    {
        if (overlayAction.mergeMode == MergeMode.REPLACE) {
            action.order = overlayAction.order;
            action.typeId = overlayAction.typeId;
            action.typeName = overlayAction.typeName;
            action.description = overlayAction.description;
            action.settings = overlayAction.settings;
            action.actionConditions = overlayAction.actionConditions;
            return;
        }
        if (!Strings.isNullOrEmpty(overlayAction.name)) {
            action.name = overlayAction.name;
        }
        if (overlayAction.order != null) {
            action.order = overlayAction.order;
        }
        if (overlayAction.typeId != null) {
            action.typeId = overlayAction.typeId;
        }
        if (!Strings.isNullOrEmpty(overlayAction.typeName)) {
            action.typeName = overlayAction.typeName;
        }
        if (!Strings.isNullOrEmpty(overlayAction.description)) {
            action.description = overlayAction.description;
        }
        for (Map.Entry<String, Object> overlaySettingsEntry : overlayAction.settings.entrySet()) {
            action.settings.put(overlaySettingsEntry.getKey(), overlaySettingsEntry.getValue());
        }

        if (overlayAction.actionConditions != null) {
            if (action.actionConditions == null) {
                action.actionConditions = overlayAction.actionConditions;
            }
            else {
                for (ConditionJson overlayActionCondition : overlayAction.actionConditions) {
                    Optional<ConditionJson> originalCondition = action.actionConditions.stream().filter(condition -> condition.name.equals(overlayActionCondition.name)).findFirst();
                    if (originalCondition.isPresent()) {
                        combineCondition(originalCondition.get(), overlayActionCondition);
                    }
                    else {
                        action.actionConditions.add(overlayActionCondition);
                    }
                }
            }
        }
    }



    private static void combineCondition(ConditionJson condition, ConditionJson overlayCondition)
    {
        ConditionAdditionalJson overlayAdditional = overlayCondition.additional;
        if (condition.additional == null) {
            condition.additional = overlayAdditional;
            return;
        }
        if (overlayAdditional != null) {
            if (!Strings.isNullOrEmpty(overlayAdditional.notes)) {
                condition.additional.notes = overlayAdditional.notes;
            }
            if (!Strings.isNullOrEmpty(overlayAdditional.type)) {
                condition.additional.type = overlayAdditional.type;
            }
            if (overlayAdditional.order != null) {
                condition.additional.order = overlayAdditional.order;
            }
        }
    }
}
