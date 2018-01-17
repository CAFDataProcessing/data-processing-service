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
package com.github.cafdataprocessing.utilities.tasksubmitter.taskmessage;

import com.hpe.caf.worker.document.DocumentWorkerDocument;
import org.apache.commons.vfs2.FileObject;

/**
 * Document representation alongside the FileObject it represents.
 */
public class DocumentAndFile {
    private final DocumentWorkerDocument document;
    private final FileObject file;

    /**
     * Create new instance for specified DocumentWorkerDocument and associated FileObject.
     * @param document A Document Worker Document.
     * @param file The File the document is associated with.
     */
    public DocumentAndFile(final DocumentWorkerDocument document, final FileObject file){
        this.document = document;
        this.file = file;
    }

    public DocumentWorkerDocument getDocument(){
        return this.document;
    }

    public FileObject getFile(){
        return this.file;
    }
}
