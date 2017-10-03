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
package com.github.cafdataprocessing.utilities.tasksubmitter;

import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used to verify that access can be obtained to a file at the current time.
 */
public class FileAccessChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileAccessChecker.class);

    /**
     * How long to wait after failed attempt to access a file before trying again.
     */
    private static final long FILE_ACCESS_ATTEMPT_WAIT_TIME = 15000L;

    /**
     * Verifies that a file is ready for use e.g. is not mid-file copy. Will wait until file is ready or return if file
     * no longer exists after waiting.
     * @return Indicates if file is ready for use.
     */
    public static boolean verifyFileReadyForUse(FileObject fileObject) throws InterruptedException, IOException {
        File fileToCheck = new File(fileObject.getName().getPath());

        byte[] buffer = new byte[4];
        boolean fileReadyForUse;
        InputStream fileInputStream = null;
        while(true){
            try{
                fileInputStream = new FileInputStream(fileToCheck);
                fileInputStream.read(buffer);
                //able to read from the file so assuming that it is available
                fileReadyForUse = true;
                break;
            }
            catch (IOException e){
                if (!fileToCheck.exists()) {
                    //Could have been deleted or copy cancelled.
                    LOGGER.debug("File detected no longer exists.");
                    fileReadyForUse = false;
                    break;
                }
                LOGGER.debug("Error encountered checking if file detected was available for use by this process. " +
                                "Will wait and then check again.",
                        e);
                Thread.sleep(FILE_ACCESS_ATTEMPT_WAIT_TIME);

            }
            finally {
                if(fileInputStream!=null) {
                    fileInputStream.close();
                }
            }
        }
        return fileReadyForUse;
    }
}
