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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cafdataprocessing.utilities.initialization.boilerplate.BoilerplateInvoker;
import com.github.cafdataprocessing.utilities.initialization.boilerplate.BoilerplateNameResolver;
import com.github.cafdataprocessing.utilities.initialization.boilerplate.CreationResultJson;
import com.google.common.base.Strings;
import com.hpe.caf.boilerplate.webcaller.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Utility class used to initialize Boilerplate using base data file.
 */
public class BoilerplateInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger(BoilerplateInitializer.class);

    public static BoilerplateNameResolver initializeBoilerplateIfRequired(String boilerplateApiUrl, String projectId,
                                                                          String boilerplateBaseDataInputFile,
                                                                          String boilerplateBaseDataOutputFile,
                                                                          boolean createBoilerplateBaseData){
        return initializeBoilerplateIfRequired(boilerplateApiUrl,
                projectId,
                boilerplateBaseDataInputFile,
                boilerplateBaseDataOutputFile,
                createBoilerplateBaseData,
                true);
    }

    public static BoilerplateNameResolver initializeBoilerplateIfRequired(String boilerplateApiUrl, String projectId,
                                                                          String boilerplateBaseDataInputFile,
                                                                          String boilerplateBaseDataOutputFile,
                                                                          boolean createBoilerplateBaseData,
                                                                          boolean overwriteExisting)
    {
        BoilerplateNameResolver nameResolver;
        if(Strings.isNullOrEmpty(boilerplateApiUrl)){
            LOG.warn("No boilerplate API URL property specified. Will be unable to resolve expression and tag names to IDs unless they are present in base data output file.");
            nameResolver = new BoilerplateNameResolver(null);
        }
        else {
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKey(projectId);
            apiClient.setBasePath(boilerplateApiUrl);
            nameResolver = new BoilerplateNameResolver(apiClient);
            if (createBoilerplateBaseData) {
                initializeBoilerplateBaseData(boilerplateBaseDataInputFile, boilerplateBaseDataOutputFile,
                        boilerplateApiUrl, overwriteExisting);
            }
        }
        if (!Strings.isNullOrEmpty(boilerplateBaseDataOutputFile)) {
            CreationResultJson boilerplateCreationResult = loadBoilerplateOutputFile(boilerplateBaseDataOutputFile);
            nameResolver.populateFromCreationResult(boilerplateCreationResult);
        }
        return nameResolver;
    }

    private static void initializeBoilerplateBaseData(String boilerplateBaseDataInputFile,
                                                      String boilerplateBaseDataOutputFile, String boilerplateApiUrl,
                                                      boolean overwriteExisting)
    {
        BoilerplateInvoker bpInvoker = new BoilerplateInvoker(boilerplateBaseDataInputFile,
                boilerplateBaseDataOutputFile,
                boilerplateApiUrl);
        bpInvoker.run(overwriteExisting);
    }

    private static CreationResultJson loadBoilerplateOutputFile(String boilerplateBaseDataOutputFile){
        //read in the output data of the created expressions
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(boilerplateBaseDataOutputFile), CreationResultJson.class);
        } catch (IOException e) {
            LOG.warn("Failed to read the output information of created boilerplate expressions and tags.", e);
        }
        return null;
    }
}
