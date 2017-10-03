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
package com.github.cafdataprocessing.utilities.tasksubmitter.monitor;

import com.github.cafdataprocessing.utilities.tasksubmitter.FileAccessChecker;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Tasked with setting up a watch on a directory for new files and perform a provided action. The watch will run in a separate thread.
 */
public class DirectoryWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryWatcher.class);

    /**
     * Delay between checks for file changes in a directory.
     */
    private static final long DIRECTORY_WATCH_DELAY_TIME = 2000L;

    /**
     * Watches a given directory for new files being added to it and when they are executes the provided function.
     * @param directoryPathStr The directory to watch.
     * @param eventFunction Function to execute when new files added to the directory. Path of new file will be passed to function.
     * @return The Executor Service used in monitoring the folder.
     * @throws IOException If unable to resolve the filepath provided.
     */
    public static ExecutorService watchDirectoryForNewFiles(String directoryPathStr, Consumer<FileObject> eventFunction) throws IOException {
        FileSystemManager fsManager = VFS.getManager();
        FileObject directoryFile = fsManager.resolveFile(directoryPathStr);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                RemoveListener removeListener = new RemoveListener(directoryFile.getFileSystem());
                DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {
                    @Override
                    public void fileDeleted(FileChangeEvent event) throws Exception {
                        //remove the listener for this file - necessary to do this as otherwise the listener event can remain after file is moved,
                        //then if the same file is added again another listener on the file is added and events double-up
                        //removeListener.remove(event.getFile(), this);
                    }

                    @Override
                    public void fileCreated(FileChangeEvent event) throws Exception {
                        FileObject createdFile = event.getFile();
                        //'imaginary' type files have been observed to be picked up when a directory of files is deleted and then
                        //re-added, looks due to children of folder not having their listeners removed causing multiple listeners
                        //to exist for a single path when files re-added
                        if (createdFile.getType() == FileType.IMAGINARY) {
                            //remove the listener that detected this file.
                            removeListener.remove(createdFile, this);
                            return;
                        }

                        //A file copied into directory may not have been fully written when create event fired.
                        //verify file is ready for use by event function.
                        if (!FileAccessChecker.verifyFileReadyForUse(createdFile)) {
                            LOGGER.debug("Detected file " + createdFile.getPublicURIString() +
                                    " has become unavailable while testing if it is ready for use. File will not be used.");
                            return;
                        }
                        //pass newly created file to the provided function
                        eventFunction.accept(createdFile);
                    }

                    @Override
                    public void fileChanged(FileChangeEvent event) throws Exception {
                        //No action on change
                    }
                });
                fm.setRecursive(true);
                fm.setDelay(DIRECTORY_WATCH_DELAY_TIME);
                fm.addFile(directoryFile);
                fm.start();
            }
        });
        return executor;
    }

    /**
     * Used to allow removal of a file from the listeners on a file monitor. Relies on the file monitor being manually set
     * before calling remove.
     */
    private static class RemoveListener {
        private FileSystem filesystem;

        public RemoveListener(FileSystem filesystem){
            this.filesystem = filesystem;
        }

        /**
         * Removes the listener associated with the provided file from the currently set rootFile.
         */
        public void remove(FileObject fileToRemove, FileListener listener){
            filesystem.removeListener(fileToRemove, listener);
        }
    }
}
