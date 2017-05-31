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

import com.github.cafdataprocessing.worker.policy.shared.Document;
import org.apache.commons.vfs2.FileObject;

/**
 * Policy Document representation alongside the FileObject it represents.
 */
public class DocumentAndFile {
    private final Document document;
    private final FileObject file;

    /**
     * Create new instance for specified Document and associated FileObject.
     * @param document A Policy Worker Document.
     * @param file The File the document is associated with.
     */
    public DocumentAndFile(Document document, FileObject file){
        this.document = document;
        this.file = file;
    }

    public Document getDocument(){
        return this.document;
    }

    public FileObject getFile(){
        return this.file;
    }
}
