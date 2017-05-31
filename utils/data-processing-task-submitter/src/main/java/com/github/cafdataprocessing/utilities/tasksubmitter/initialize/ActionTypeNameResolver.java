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
package com.github.cafdataprocessing.utilities.tasksubmitter.initialize;

import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionTypesApi;
import com.github.cafdataprocessing.processing.service.client.model.ExistingActionType;
import com.github.cafdataprocessing.processing.service.client.model.ExistingActionTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves action type IDs from names and vice-versa.
 */
public class ActionTypeNameResolver {
    private final Map<String, Long> actionTypesNamesToIds = new LinkedHashMap<>();
    private final Map<Long, String> actionTypesIdsToNames = new LinkedHashMap<>();

    private final static int pageSize = 100;

    /**
     * Records action type name and ID information  for future resolution by querying provided action type API with the project ID.
     * @param actionTypesApi API to call to retreive action type information.
     * @param projectId ProjectId to send query requests with.
     * @throws ApiException Thrown if there is an error communicating with the API or if error response received from API.
     */
    public void populateActionTypesFromApi(ActionTypesApi actionTypesApi, String projectId) throws ApiException {
        int pageNum = 1;
        int totalHits;
        do {
            ExistingActionTypes actionTypesResult = actionTypesApi.getActionTypes(projectId, pageNum, pageSize);
            List<ExistingActionType> actionTypes = actionTypesResult.getActionTypes();
            for (ExistingActionType actionType : actionTypes) {
                if (!actionTypesNamesToIds.containsKey(actionType.getInternalName())) {
                    actionTypesNamesToIds.put(actionType.getInternalName(), actionType.getId());
                }
                if(!actionTypesIdsToNames.containsKey(actionType.getId())){
                    actionTypesIdsToNames.put(actionType.getId(), actionType.getInternalName());
                }
            }
            totalHits = actionTypesResult.getTotalHits();
            pageNum++;
        }
        while(totalHits > pageNum * pageSize);
    }

    /**
     * Get the map of action type names to IDs.
     * @return Map of action type names to IDs.
     */
    public Map<String, Long> getActionTypesNamesToIds(){
        return this.actionTypesNamesToIds;
    }

    /**
     * Get the map of action type IDs to names.
     * @return Map of action type IDs to names.
     */
    public Map<Long, String> getActionTypesIdsToNames(){
        return this.actionTypesIdsToNames;
    }
}
