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
package com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage;

import com.hpe.caf.api.worker.TaskMessage;
import org.apache.commons.vfs2.FileObject;

/**
 * Task Message and the File it represents
 */
public class FileAndTaskMessage {
    private FileObject file;
    private TaskMessage taskMessage;

    /**
     * Create new instance for specified FileObject and TaskMessage.
     * @param file FileObject that task message is associated with.
     * @param taskMessage TaskMessage that was built for the file.
     */
    public FileAndTaskMessage(FileObject file, TaskMessage taskMessage){
        this.file = file;
        this.taskMessage = taskMessage;
    }

    public FileObject getFile(){
        return this.file;
    }

    public TaskMessage getTaskMessage(){
        return this.taskMessage;
    }
}
