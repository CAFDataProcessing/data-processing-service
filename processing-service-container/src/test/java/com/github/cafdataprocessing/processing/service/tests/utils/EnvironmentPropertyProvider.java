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

/**
 * Provides access to environment variables for use with tests.
 */
public class EnvironmentPropertyProvider {
    private static final String host;

    static {
        host = System.getenv("processing.webservice.host");
    }

    public static String getHost(){
        return host;
    }

    /**
     * Returns URI to make requests against the API controllers (aside from swagger UI and contract)
     * @return
     */
    public static String getWebServiceUrl(){
        return EnvironmentPropertyProvider.getHost() + "/data-processing-service/v1";
    }
}
