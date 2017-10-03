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
package com.github.cafdataprocessing.utilities.tasksubmitter.services;

import com.github.cafdataprocessing.utilities.tasksubmitter.properties.StorageProperties;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.hpe.caf.worker.datastore.fs.FileSystemDataStore;
import com.hpe.caf.worker.datastore.fs.FileSystemDataStoreConfiguration;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Provides access to filesystem storage configuration and operations.
 */
public class FileSystemStorageServices {
    private static FileSystemStorageServices instance;

    private final FileSystemDataStore dataStore;
    private final FileSystemDataStoreConfiguration storageConfiguration;
    private final StorageProperties storageProperties;

    /**
     * Retrieves the shared instance of FileSystemStorageServices.
     * @return Instance of FileSystemStorageServices.
     */
    public static FileSystemStorageServices getInstance() {
        if (instance == null) {
            instance = new FileSystemStorageServices();
        }
        return instance;
    }

    private FileSystemStorageServices() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBeanDefinition("StorageProperties", new RootBeanDefinition(StorageProperties.class));
        context.refresh();
        storageProperties = context.getBean(StorageProperties.class);
        storageConfiguration = buildStorageConfiguration();
        dataStore = createDataStore(storageConfiguration);
    }

    public ManagedDataStore getDataStore(){
        return this.dataStore;
    }

    public FileSystemDataStoreConfiguration getStorageConfiguration(){
        return this.storageConfiguration;
    }

    public StorageProperties getStorageProperties(){
        return this.storageProperties;
    }

    private FileSystemDataStoreConfiguration buildStorageConfiguration(){
        FileSystemDataStoreConfiguration configuration = new FileSystemDataStoreConfiguration();
        configuration.setDataDir(storageProperties.getDataDirectory());
        return configuration;
    }

    private static FileSystemDataStore createDataStore(FileSystemDataStoreConfiguration storageConfiguration){
        try {
            return new FileSystemDataStore(storageConfiguration);
        } catch (DataStoreException e) {
            throw new RuntimeException("Failure creating filesystem data store. Check your storage configuration is correct.",
                    e);
        }
    }
}
