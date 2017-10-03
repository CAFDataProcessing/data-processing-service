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
package com.github.cafdataprocessing.utilities.taskreceiver;

import com.github.cafdataprocessing.utilities.taskreceiver.taskoutput.OutputHelper;
import org.apache.commons.io.IOUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Mock of OutputHelper for test purposes which records output data in memory.
 */
public class MockedOutputHelper {
    private OutputHelper outputHelper = Mockito.mock(OutputHelper.class);
    private HashMap<String, InputStream> localOutputStorage;

    public MockedOutputHelper() throws IOException {
        localOutputStorage = new HashMap<>();

        when(outputHelper.createFolderFromDocument(Mockito.any(),
                Mockito.any())).thenCallRealMethod();
        when(outputHelper.createFolderFromTaskMessageId(Mockito.any(), Mockito.any()))
                .thenCallRealMethod();

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                addToLocalOutputStorage((String) args[0], (String) args[1], (InputStream) args[2]);
                return null;
            }
        }).when(outputHelper).outputStream(Mockito.anyString(), Mockito.anyString(), Mockito.any());

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                addToLocalOutputStorage((String) args[0], (String) args[1], (String) args[2] );
                return null;
            }
        }).when(outputHelper).outputString(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    public HashMap<String, InputStream> getLocalStorage(){
        return this.localOutputStorage;
    }

    public OutputHelper getOutputHelper(){
        return this.outputHelper;
    }

    private void addToLocalOutputStorage(String outputPath, String filename, InputStream outputStream){
        if(localOutputStorage.containsKey(outputPath)){
            throw new RuntimeException("Test tried to output to a path that was already output during this test run. Path: "
                    +outputPath + File.separator + filename);
        }
        localOutputStorage.put(outputPath + File.separator +filename, outputStream);
    }

    private void addToLocalOutputStorage(String outputPath, String filename, String outputString){
        if(localOutputStorage.containsKey(outputPath)){
            throw new RuntimeException("Test tried to output to a path that was already output during this test run. Path: "
                    +outputPath + File.separator + filename);
        }
        try {
            localOutputStorage.put(outputPath + File.separator +filename, IOUtils.toInputStream(outputString, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failure during test while converting string that was to be stored in output storage.",
                    e);
        };
    }
}
