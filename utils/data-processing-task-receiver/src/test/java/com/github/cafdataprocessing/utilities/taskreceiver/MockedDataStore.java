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
package com.github.cafdataprocessing.utilities.taskreceiver;

import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.hpe.caf.worker.datastore.fs.FileSystemDataStore;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

/**
 * A mocked up data store that stores files in memory
 */
public class MockedDataStore {
    private final ManagedDataStore dataStore = Mockito.mock(FileSystemDataStore.class);
    private HashMap<String, InputStream> localDataStore = new HashMap<>();

    private MockedDataStore(){
        try {
            when(dataStore.store(Mockito.any(InputStream.class), Mockito.anyString())).thenAnswer(new Answer() {
                public Object answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    return storeInLocalDataStore((InputStream)args[0], (String)args[1]);
                }
            });
            when(dataStore.retrieve(Mockito.anyString())).thenAnswer(new Answer() {
                public Object answer(InvocationOnMock invocation) throws DataStoreException {
                    Object[] args = invocation.getArguments();
                    return retrieveFromLocalDataStore((String) args[0]);
                }
            });
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws DataStoreException {
                    Object[] args = invocation.getArguments();
                    deleteFromLocalDataStore((String) args[0]);
                    return null;
                }
            }).when(dataStore).delete(Mockito.anyString());
        } catch (DataStoreException e) {
            throw new RuntimeException("Failure mocking data store.", e);
        }
    }

    public HashMap<String, InputStream> getLocalDataStore(){
        return this.localDataStore;
    }

    public static ManagedDataStore getDataStore(){
        return (new MockedDataStore()).dataStore;
    }

    private String storeInLocalDataStore(InputStream stream, String outputReference){
        String storageReferenceKey = UUID.randomUUID().toString();
        localDataStore.put(storageReferenceKey, stream);
        return storageReferenceKey;
    }

    private InputStream retrieveFromLocalDataStore(String storageReferenceKey) throws DataStoreException {
        InputStream matchedStream = localDataStore.get(storageReferenceKey);
        if(matchedStream==null){
            throw new DataStoreException("Failed to retrieve data");
        }
        return matchedStream;
    }

    private void deleteFromLocalDataStore(String storageReferenceKey) throws DataStoreException {
        InputStream matchedStream = localDataStore.remove(storageReferenceKey);
    }
}
