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

import com.github.cafdataprocessing.utilities.tasksubmitter.services.Services;
import com.github.cafdataprocessing.worker.policy.shared.Document;
import com.hpe.caf.api.ConfigurationException;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.github.cafdataprocessing.utilities.tasksubmitter.FileAccessChecker;
import org.apache.commons.vfs2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tasked with providing Policy Documents representing provided folder or file location
 */
public class PolicyDocumentsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDocumentsBuilder.class);


    private static final ManagedDataStore dataStore = Services.getInstance().getStorageDataStore();
    private static final String dataDir = Services.getInstance().getStorageConfiguration().getDataDir();

    /**
     * Constructs a Policy Worker Document with basic metadata for specified document path.
     * @param docPath Path to a document file to base Policy Worker Document on.
     * @return Constructed Policy Worker Document and associated file.
     * @throws IOException If error occurs accessing file at provided path.
     * @throws DataStoreException If error occurs storing data for file.
     */
    public static DocumentAndFile getDocument(String docPath) throws IOException, DataStoreException {
        if ( docPath == null )
        {
            throw new NullPointerException("Path passed to build policy document cannot be null.");
        }
        FileSystemManager fsManager = VFS.getManager();
        FileObject file = fsManager.resolveFile(docPath);
        // get the data store reference
        String dataStoreReference = getDataStoreReferenceForFile(file);
        //Construct Policy Worker Document object, and put on the list of data processing documents to be sent.
        Document builtDocument = buildPolicyDocument(dataStoreReference, getDocumentReference(file));
        builtDocument.getMetadata().put("FILESIZE_BYTES", String.valueOf(new File(file.getName().getPath()).length()));
        return new DocumentAndFile(builtDocument, file);
    }

    /**
     * Constructs a list of Policy Worker Documents with basic metadata for each file filesToSendLocation directory.
     *
     * Uses fields....
     * dataStore   The DataStore to store the file to.
     * filesToSendLocation The directory of files to process
     *
     * @param directory Directory containing files to construct Policy Documents from.
     *
     * @return List of Documents and associated files.
     * @throws ConfigurationException If path provided is not a directory.
     * @throws IOException If error occurs accessing files in directory.
     * @throws DataStoreException If error occurs storing data for file.
     */
    public static List<DocumentAndFile> getDocuments(String directory) throws ConfigurationException,
            IOException, DataStoreException, InterruptedException {
        if ( directory == null )
        {
            throw new NullPointerException("Directory passed to read documents from should not be null.");
        }
        // final list of Documents to be send for data processing.
        ArrayList<DocumentAndFile> dataProcessingDocumentsList = new ArrayList<>();

        FileSystemManager fsManager = VFS.getManager();
        FileObject folder = fsManager.resolveFile(directory);

        if (!folder.isFolder()) {
            throw new ConfigurationException(
                    "Location to read documents from is not a directory." + directory);
        }

        for (FileObject fileObject : folder.getChildren()) {
            //if the file is a directory then add all documents in that folder
            if (fileObject.isFolder()) {
                dataProcessingDocumentsList.addAll(getDocuments(fileObject.getName().getURI()));
                continue;
            }
            //verify that we can access the file (i.e. it is not in use)
            if(!FileAccessChecker.verifyFileReadyForUse(fileObject)){
                //unable to access file, do not try to build a message for it
                LOGGER.warn("Unable to access detected file "+fileObject.getPublicURIString() + ". " +
                        "Another process may be using it or it may have been deleted. File will not be used.");
                continue;
            }

            // get the data store reference
            String dataStoreReference = getDataStoreReferenceForFile(fileObject);
            //Construct Policy Worker Document object, and put on the list of data processing documents to be sent.
            Document builtDocument = buildPolicyDocument(dataStoreReference, getDocumentReference(fileObject));

            builtDocument.getMetadata().put("FILESIZE_BYTES", String.valueOf(new File(fileObject.getName().getPath()).length()));

            dataProcessingDocumentsList.add(new DocumentAndFile(builtDocument, fileObject));
        }

        return dataProcessingDocumentsList;
    }

    private static Document buildPolicyDocument( String dataStoreReference, String documentReference)
    {
        Document document = new Document();
        //Add metadata for workflow
        document.getMetadata().put( "storageReference", dataStoreReference );
        //Required for workflow
        document.setReference( documentReference );
        //adding the name of the file. If we were provided the full path to the file then omit everything except the
        //text after last backslash (expecting name of the file from that point).
        int lastBackslashIndex = documentReference.lastIndexOf("/");
        if(lastBackslashIndex==-1){
           document.getMetadata().put("FILENAME", documentReference);
        }
        else{
            document.getMetadata().put("FILENAME", documentReference.substring(lastBackslashIndex+1));
        }

        return document;
    }

    private static String getDataStoreReferenceForFile(FileObject file) throws DataStoreException, IOException {
        try{
            File f = new File(file.getName().getPath());
            return dataStore.store(f.toPath(), dataDir);
        }
        catch(Exception e){
            throw new DataStoreException("Failure storing file. File path: "+file.getName().getPath(), e);
        }
    }

    private static String getDocumentReference(FileObject file){
        return file.getName().getPath();
    }
}
