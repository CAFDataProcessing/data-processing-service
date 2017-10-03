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
package com.github.cafdataprocessing.utilities.initialization;

import com.github.cafdataprocessing.utilities.initialization.jsonobjects.WorkflowJson;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parameters to use for method calls to the WorkflowInitializer class.
 */
public class WorkflowInitializationParams {
    /**
     * ProjectId to create workflow under.
     */
    private final String projectId;
    /**
     * Path to file containing workflow in JSON format that should be created.
     */
    private String workflowBaseDataFileName = null;

    /**
     * Input stream containing workflow in JSON format that should be created.
     */
    private InputStream workflowBaseDataStream = null;
    /**
     * Path to file containing extensions / overrides to merge with the base
     * workflow
     */
    private String workflowOverlayFileName = null;
    /**
     * Input stream containing extensions / overrides to merge with the base
     * workflow
     */
    private InputStream workflowOverlayStream = null;
    /**
     * Whether existing workflows under the {@code projectId} with the same name as the provided workflow should be removed.
     */
    private boolean overwriteExisting = true;

    /**
     * Create the parameter object with required properties.
     * @param workflowBaseDataFileName Path to file containing workflow in JSON format that should be created.
     * @param projectId ProjectId to create workflow under.
     */
    public WorkflowInitializationParams(String workflowBaseDataFileName, String projectId){
        this.workflowBaseDataFileName = workflowBaseDataFileName;
        this.projectId = projectId;
    }

    /**
     * Create the parameter object with required properties.
     * @param workflowBaseDataFileName Path to file containing workflow in JSON format that should be created.
     * @param workflowOverlayFileName Path to file containing extensions / overrides to merge with the base.
     * @param projectId ProjectId to create workflow under.
     */
    public WorkflowInitializationParams(String workflowBaseDataFileName, String workflowOverlayFileName, String projectId){
        this.workflowBaseDataFileName = workflowBaseDataFileName;
        this.workflowOverlayFileName = workflowOverlayFileName;
        this.projectId = projectId;
    }

    /**
     * Create the parameter object with required properties.
     * @param workflowBaseDataStream Input stream containing workflow in JSON format that should be created.
     * @param projectId ProjectId to create workflow under.
     */
    public WorkflowInitializationParams(InputStream workflowBaseDataStream, String projectId){
        this.workflowBaseDataStream = workflowBaseDataStream;
        this.projectId = projectId;
    }

    /**
     * Create the parameter object with required properties.
     * @param workflowBaseDataStream Input stream containing workflow in JSON format that should be created.
     * @param workflowOverlayStream InputStream containing extensions / overrides to merge with the base
     * workflow
     * @param projectId ProjectId to create workflow under.
     */
    public WorkflowInitializationParams(InputStream workflowBaseDataStream, InputStream workflowOverlayStream, String projectId){
        this.workflowBaseDataStream = workflowBaseDataStream;
        this.workflowOverlayStream = workflowOverlayStream;
        this.projectId = projectId;
    }

    /**
     * Get whether existing workflows under the {@code projectId} with the same name as the provided workflow should be removed.
     */
    public boolean getOverwriteExisting(){
        return this.overwriteExisting;
    }

    public String getProjectId(){
        return this.projectId;
    }
    public String getWorkflowBaseDataFileName(){
        return this.workflowBaseDataFileName;
    }

    /**
     * Builds a WorkflowJson object using the workflowBaseDataStream if set or the workflowBaseDataFileName. Returns
     * null if neither is set.
     * @return Constructed WorkflowJson form overlay settings or null if neither workflowBaseDataStream or
     * workflowBaseDataFileName have been set.
     * @throws IOException IOException If unable to convert Input Stream or file contents to WorkflowJson object.
     */
    public WorkflowJson getWorkflowBaseDataJson() throws IOException {
        if(workflowBaseDataStream==null){
            if(Strings.isNullOrEmpty(workflowBaseDataFileName)){
                return null;
            }
            return WorkflowJson.readInputFile(workflowBaseDataFileName);
        }
        return WorkflowJson.readInputStream(workflowBaseDataStream);
    }

    public InputStream getWorkflowBaseDataStream(){
        return this.workflowBaseDataStream;
    }

    public String getWorkflowOverlayFileName(){
        return this.workflowOverlayFileName;
    }

    /**
     * Builds a WorkflowJson object using the workflowOverlayStream if set or the workflowOverlayFileName. Returns
     * null if neither is set.
     * @return Constructed WorkflowJson form overlay settings or null if neither workflowOverlayStream or
     * workflowOverlayFileName have been set.
     * @throws IOException IOException If unable to convert Input Stream or file contents to WorkflowJson object.
     */
    public WorkflowJson getWorkflowOverlayJson() throws IOException {
        if(workflowOverlayStream==null){
            if(Strings.isNullOrEmpty(workflowOverlayFileName)){
                return null;
            }
            return WorkflowJson.readInputFile(workflowOverlayFileName);
        }
        return WorkflowJson.readInputStream(workflowOverlayStream);
    }

    /**
     * Get InputStream containing extensions / overrides to merge with the base
     * workflow.
     */
    public InputStream getWorkflowOverlayStream(){
        return this.workflowOverlayStream;
    }

    /**
     * Set whether existing workflows under the {@code projectId} with the same name as the provided workflow should be removed.
     * @param overwriteExisting Value to set overwriteExisting to.
     */
    public void setOverwriteExisting(boolean overwriteExisting){
        this.overwriteExisting = overwriteExisting;
    }

    /**
     * Set Path to file containing extensions / overrides to merge with the base
     * workflow.
     * @param workflowOverlayFileName Path to file containing extensions / overrides to merge with the base
     * workflow
     */
    public void setWorkflowOverlayFileName(String workflowOverlayFileName){
        this.workflowOverlayFileName = workflowOverlayFileName;
    }

    /**
     * Set InputStream containing extensions / overrides to merge with the base
     * workflow.
     * @param workflowOverlayStream InputStream containing extensions / overrides to merge with the base
     * workflow
     */
    public void setWorkflowOverlayStream(InputStream workflowOverlayStream){
        this.workflowOverlayStream = workflowOverlayStream;
    }
}
