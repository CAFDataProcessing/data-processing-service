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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.processing.service.client.ApiClient;
import com.github.cafdataprocessing.processing.service.client.ApiException;
import com.github.cafdataprocessing.processing.service.client.api.ActionTypesApi;
import com.github.cafdataprocessing.processing.service.client.model.ActionType;
import com.github.cafdataprocessing.processing.service.client.model.ExistingActionType;
import com.github.cafdataprocessing.processing.service.client.model.ExistingActionTypes;
import com.github.cafdataprocessing.processing.service.tests.utils.ApiClientProvider;
import com.github.cafdataprocessing.processing.service.tests.utils.ExampleActionTypeDefinition;
import com.github.cafdataprocessing.processing.service.tests.utils.ObjectsInitializer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Integration tests for Action Type API paths in the Data Processing Service.
 */
public class ActionTypesIT {
    private static ObjectMapper mapper = new ObjectMapper();

    private String projectId;
    private static ActionTypesApi actionTypesApi;

    @BeforeClass
    public static void setup() throws Exception {
        ApiClient apiClient = ApiClientProvider.getApiClient();
        actionTypesApi = new ActionTypesApi(apiClient);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Before every test generate a new project ID to avoid results from previous tests affecting subsequent tests.
     */
    @BeforeMethod
    public void intializeProjectId(){
        projectId = UUID.randomUUID().toString();
    }

    @Test(description = "Creates some action types and retrieves them individually.")
    public void createAndGetActionType() throws ApiException {
        ActionType actionTypeToCreate_1 = ObjectsInitializer.initializeActionType(null);
        ExistingActionType createdActionType_1 = actionTypesApi.createActionType(projectId,
                actionTypeToCreate_1);
        compareActionTypes(actionTypeToCreate_1, createdActionType_1);

        ActionType actionTypeToCreate_2 = ObjectsInitializer.initializeActionType(null);
        ExistingActionType createdActionType_2 = actionTypesApi.createActionType(projectId,
                actionTypeToCreate_2);
        compareActionTypes(actionTypeToCreate_2, createdActionType_2);

        //retrieve the action types
        ExistingActionType retrievedActionType_1 = actionTypesApi.getActionType(projectId, createdActionType_1.getId());
        Assert.assertEquals(retrievedActionType_1.getId(), createdActionType_1.getId(),
                "ID on retrieved Action Type should be the ID requested. First action type.");
        compareActionTypes(actionTypeToCreate_1, retrievedActionType_1);

        ExistingActionType retrievedActionType_2 = actionTypesApi.getActionType(projectId, createdActionType_2.getId());
        Assert.assertEquals(retrievedActionType_2.getId(), createdActionType_2.getId(),
                "ID on retrieved Action Type should be the ID requested. Second action type.");
        compareActionTypes(actionTypeToCreate_2, retrievedActionType_2);
    }

    @Test(description = "Tries to retrieve an action type with incorrect ID.")
    public void getActionTypeByIncorrectId() throws ApiException {
        try{
            actionTypesApi.getActionType(projectId, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception thrown trying to retrieve Action Type with ID that doesn't exist.");
        } catch (ApiException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to find Action Type with ID"),
                    "Expected message on exception to say the action type was not found. Message: "+e.getMessage());
        }
        //verify that even with an action type created under this project ID that incorrect ID does not just return the other action type
        ActionType actionTypeToCreate_1 = ObjectsInitializer.initializeActionType(null);
        actionTypesApi.createActionType(projectId,
                actionTypeToCreate_1);
        try{
            actionTypesApi.getActionType(projectId, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception thrown trying to retrieve Action Type with ID that doesn't exist.");
        } catch (ApiException e) {
            Assert.assertTrue(e.getMessage().contains("Unable to find Action Type with ID"),
                    "Expected message on exception to say the action type was not found on second try. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates then updates an action type.")
    public void updateActionType() throws ApiException {
        ActionType actionTypeToCreate_1 = ObjectsInitializer.initializeActionType(null);
        ExistingActionType createdActionType_1 = actionTypesApi.createActionType(projectId,
                actionTypeToCreate_1);

        ActionType actionTypeToCreate_2 = ObjectsInitializer.initializeActionType(null);
        ExistingActionType createdActionType_2 = actionTypesApi.createActionType(projectId,
                actionTypeToCreate_2);

        ActionType actionTypeToUpdate_1 = ObjectsInitializer.initializeActionType(null);
        actionTypesApi.updateActionType(projectId, createdActionType_1.getId(), actionTypeToUpdate_1);

        //verify that updated action type and non-updated action type are as expected
        ExistingActionType retrievedActionType_1 = actionTypesApi.getActionType(projectId, createdActionType_1.getId());
        compareActionTypes(actionTypeToUpdate_1, retrievedActionType_1);

        ExistingActionType retrievedActionType_2 = actionTypesApi.getActionType(projectId, createdActionType_2.getId());
        compareActionTypes(actionTypeToCreate_2, retrievedActionType_2);

        //verify that trying to update with an ID that doesn't exist throws an exception
        try{
            actionTypesApi.updateActionType(projectId, ThreadLocalRandom.current().nextLong(), actionTypeToUpdate_1);
            Assert.fail("Expected exception to occur when updating action type with an ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Action Type with ID"),
                    "Expected message on exception to say the action type was not found. Message: "+e.getMessage());
        }
    }

    @Test(description = "Creates some action types then deletes.")
    public void deleteActionType() throws ApiException {
        final int numberOfDefaultTypes = 4;
        ActionType typeToCreate_1 = ObjectsInitializer.initializeActionType(null);

        ExistingActionType createdType_1 = actionTypesApi.createActionType(projectId, typeToCreate_1);

        ActionType typeToCreate_2 = ObjectsInitializer.initializeActionType(null);
        ExistingActionType createdType_2 = actionTypesApi.createActionType(projectId, typeToCreate_2);

        //delete the first action type
        actionTypesApi.deleteActionType(projectId, createdType_1.getId());

        //verify that the processing rule no longer exists on the workflow
        ExistingActionTypes actionTypesResult = actionTypesApi.getActionTypes(projectId,
                1, 100);
        //NOTE that default Policy database spun up has four policy types by default that are returned we actually have five on
        // the page results.
        Assert.assertEquals((long) actionTypesResult.getTotalHits(), 1 + numberOfDefaultTypes,
                "Total Hits should be five after deleting an action type.");
        Assert.assertEquals(actionTypesResult.getActionTypes().size(), 1 + numberOfDefaultTypes,
                "Five action types should be returned in the results.");

        //check the default action types are present
        List<ExistingActionType> retrievedActionTypes = actionTypesResult.getActionTypes();
        long numDefaultsFound = retrievedActionTypes.stream()
                .filter(type -> type.getInternalName().equals("MetadataPolicy") || type.getInternalName().equals("ExternalPolicy")
                || type.getInternalName().equals("ChainedActionType") || type.getInternalName().equals("FieldMappingActionType"))
                .count();
        Assert.assertEquals(numDefaultsFound, numberOfDefaultTypes,
                "Expecting to find four default action types returned on get request.");

        Optional<ExistingActionType> remainingTypeCheck = actionTypesResult.getActionTypes().stream()
                .filter(type -> type.getId().equals(createdType_2.getId())).findFirst();
        if(!remainingTypeCheck.isPresent()){
            Assert.fail("Action Type that was expected to be returned on the page request was not returned.");
        }
        ExistingActionType remainingType = remainingTypeCheck.get();
        compareActionTypes(typeToCreate_2, remainingType);

        Assert.assertEquals(remainingType.getId(), createdType_2.getId(),
                "ID of remaining action type should be the second action type that was created.");
        try{
            actionTypesApi.getActionType(projectId, createdType_1.getId());
            Assert.fail("Expecting exception to be thrown when trying to retrieve deleted action type.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Action Type with ID"),
                    "Exception message should contain expected message about not finding action type.");
        }

        //try to delete an action type that doesn't exist
        try{
            actionTypesApi.deleteActionType(projectId, ThreadLocalRandom.current().nextLong());
            Assert.fail("Expected exception to have been thrown trying to delete action type with an ID that doesn't exist.");
        }
        catch(ApiException e){
            Assert.assertTrue(e.getMessage().contains("Unable to find Action Type with ID"),
                    "Expecting exception message to mention unable to find action type. Messages was: "+e.getMessage());
        }
    }

    @Test(description = "Creates action types and then retrieves pages of them.")
    public void getActionTypes() throws ApiException {
        int numberOfTypesToCreate_1 = 28;
        final int numberOfDefaults = 4;

        // verify that only defaults are returned initially
        pageThroughTypes(100, new LinkedList<>(), numberOfDefaults);

        List<ExistingActionType> createdTypes_1 = createMultipleActionTypes(numberOfTypesToCreate_1);

        // page through action types. Should find all that were created. Note adding 4 here to accommodate the default policy
        // types that will come back.
        int pageSize = 5;
        //creating copy as items will be removed the list during page method.
        LinkedList<ExistingActionType> copyOfCreatedTypes = new LinkedList<>();
        copyOfCreatedTypes.addAll(createdTypes_1);
        pageThroughTypes(pageSize, copyOfCreatedTypes, createdTypes_1.size() + numberOfDefaults);
        //change page size to verify that parameter is respected
        pageSize = 30;

        pageThroughTypes(pageSize, createdTypes_1, createdTypes_1.size()+numberOfDefaults);
    }

    private void pageThroughTypes(int pageSize, List<ExistingActionType> typesToFind,
                                            int expectedNumberOfTypes) throws ApiException {
        int typesSoFarCount = 0;
        int pageNum = 1;
        while(true){
            ExistingActionTypes typesPage = actionTypesApi.getActionTypes(projectId,
                    pageNum, pageSize);
            Assert.assertEquals((long)typesPage.getTotalHits(), expectedNumberOfTypes,
                    "Total hits should be the same as the expected number of action types.");

            List<ExistingActionType> retrievedTypes = typesPage.getActionTypes();

            if(pageNum*pageSize <= expectedNumberOfTypes){
                //until we get to the page that includes the last result (or go beyond the number available)
                //there should always be 'pageSize' number of results returned
                Assert.assertEquals(retrievedTypes.size(), pageSize, "Expecting full page of action type results.");
            }
            //remove returned types from list of types to find
            checkTypesReturned(typesPage, typesToFind);
            //increment page num so that next call retrieves next page
            pageNum++;
            typesSoFarCount += retrievedTypes.size();
            if(typesSoFarCount > expectedNumberOfTypes){
                Assert.fail("More types encountered than expected.");
            }
            else if(typesSoFarCount == expectedNumberOfTypes){
                Assert.assertTrue(typesToFind.isEmpty(),
                        "After encountering the expected number of types there should be no more types that we are searching for.");
                break;
            }
        }
        //send a final get request and verify that nothing is returned.
        ExistingActionTypes expectedEmptyGetResult = actionTypesApi.getActionTypes(projectId,
                pageNum, pageSize);

        Assert.assertEquals((long) expectedEmptyGetResult.getTotalHits(), expectedNumberOfTypes,
                "Total hits should report the expected number of action types even on a page outside range of results.");
        Assert.assertTrue(expectedEmptyGetResult.getActionTypes().isEmpty(),
                "Should be no action types returned for page request outside expected range.");
    }

    private void checkTypesReturned(ExistingActionTypes retrievedTypes,
                                              List<ExistingActionType> typesToFind){
        for(ExistingActionType retrievedType: retrievedTypes.getActionTypes()){
            if(retrievedType.getInternalName().equals("ExternalPolicy") ||
                    retrievedType.getInternalName().equals("MetadataPolicy")
                    || retrievedType.getInternalName().equals("ChainedActionType")
                    || retrievedType.getInternalName().equals("FieldMappingActionType")){
                //default types in policy database, ignore these and continue
                continue;
            }

            Optional<ExistingActionType> foundType = typesToFind.stream()
                    .filter(filterType -> filterType.getId().equals(retrievedType.getId())).findFirst();
            if(foundType.isPresent()) {
                compareActionTypes(foundType.get(), retrievedType);
                //remove from the list of action types for next check
                typesToFind.remove(foundType.get());
            }
            else{
                Assert.fail("An unexpected action type was returned.");
            }
        }
    }

    private List<ExistingActionType> createMultipleActionTypes(int numberOfTypesToCreate) throws ApiException {
        List<ExistingActionType> createdTypes = new LinkedList<>();
        for(int numberOfTypesCreated = 0; numberOfTypesCreated < numberOfTypesToCreate; numberOfTypesCreated++){
            //create an action type
            ActionType newType = ObjectsInitializer.initializeActionType(null);
            ExistingActionType createdType = actionTypesApi.createActionType(projectId,
                    newType);
            createdTypes.add(createdType);
        }
        return createdTypes;
    }

    private void compareActionTypes(ActionType expectedActionType, ExistingActionType retrievedActionType) {
        Assert.assertEquals(retrievedActionType.getName(), expectedActionType.getName(),
                "Name on Action Type should be as expected.");
        Assert.assertEquals(retrievedActionType.getDescription(), expectedActionType.getDescription(),
                "Description on Action Type should be as expected.");
        Assert.assertEquals(retrievedActionType.getInternalName(), expectedActionType.getInternalName(),
                "Internal Name on Action Type should be as expected.");
        ExampleActionTypeDefinition retrievedDefinition = mapper.convertValue(retrievedActionType.getDefinition(),
                ExampleActionTypeDefinition.class);
        ExampleActionTypeDefinition expectedActionTypeDefinition = mapper.convertValue(expectedActionType.getDefinition(),
                ExampleActionTypeDefinition.class);

        //compare definitions
        Assert.assertEquals(retrievedDefinition.getTitle(), expectedActionTypeDefinition.getTitle(),
                "Title on Action Type definition property should be as expected.");
        Assert.assertEquals(retrievedDefinition.getDescription(), expectedActionTypeDefinition.getDescription(),
                "Description on Action Type definition property should be as expected.");
        Assert.assertEquals(retrievedDefinition.getType(), expectedActionTypeDefinition.getType(),
                "Title on Action Type definition property should be as expected.");

        ExampleActionTypeDefinition.compareProperties(expectedActionTypeDefinition.getProperties(),
                retrievedDefinition.getProperties());
    }
}
