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

import com.github.cafdataprocessing.entity.fields.*;
import com.github.cafdataprocessing.worker.policy.handlers.shared.document.SharedDocument;
import com.hpe.caf.api.worker.DataStoreException;
import com.hpe.caf.api.worker.ManagedDataStore;
import com.hpe.caf.util.ref.ReferencedData;

import java.io.InputStream;
import java.util.*;

/**
 * Methods to retreive data representing documents for use with tests.
 */
public class TestDocumentData {
    private final ManagedDataStore dataStore;
    private final String storeOutputLocation;

    private String docxStoreRef;
    private String docxContentStoreRef;
    private String testFieldRef;

    private String jpgStoreRef;
    private String jpgContentStoreRef;

    private String zipFilename = "input/zip/Level1of2.zip";
    private String zipStoreRef;
    private String zipLevel1DocFilename = "input/zip/Level1.docx";
    private String zipLevel1DocStoreRef;
    private String zipLevel1DocContentRef;
    private String zipLevel2Filename = "input/zip/Level2.zip";
    private String zipLevel2StoreRef;
    private String zipLevel2DocFilename = "input/zip/Level2.docx";
    private String zipLevel2DocStoreRef;
    private String zipLevel2DocContentRef;

    public TestDocumentData(ManagedDataStore dataStore, String storeOutputLocation){
        this.dataStore = dataStore;
        this.storeOutputLocation = storeOutputLocation;
    }

    public void storeAllDocs() throws DataStoreException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        storeDocxData(classLoader);
        storeJpgData(classLoader);
        storeZipData(classLoader);
    }

    public void cleanupStoredData() throws DataStoreException {
        cleanupDocxStoredData();
        cleanupStoredJpgData();
        cleanupStoredZipData();
    }

    private void cleanupDocxStoredData() throws DataStoreException {
        dataStore.delete(docxStoreRef);
        dataStore.delete(docxContentStoreRef);
    }

    private void cleanupStoredJpgData() throws DataStoreException {
        dataStore.delete(jpgStoreRef);
        dataStore.delete(jpgContentStoreRef);
    }

    private void cleanupStoredZipData() throws DataStoreException {
        dataStore.delete(zipStoreRef);
        dataStore.delete(zipLevel1DocStoreRef);
        dataStore.delete(zipLevel1DocContentRef);
        dataStore.delete(zipLevel2StoreRef);
        dataStore.delete(zipLevel2DocStoreRef);
        dataStore.delete(zipLevel2DocContentRef);
    }

    private void storeZipData(ClassLoader classLoader) throws DataStoreException {
        InputStream zipStream = classLoader.getResourceAsStream(zipFilename);
        zipStoreRef = dataStore.store(zipStream, storeOutputLocation);
        InputStream zipLevel1DocStream = classLoader.getResourceAsStream(zipLevel1DocFilename);
        zipLevel1DocStoreRef = dataStore.store(zipLevel1DocStream, storeOutputLocation);
        InputStream zipLevel1DocContentStream = classLoader.getResourceAsStream(zipLevel1DocFilename +".content");
        zipLevel1DocContentRef = dataStore.store(zipLevel1DocContentStream, storeOutputLocation);

        InputStream zipLevel2Stream =classLoader.getResourceAsStream(zipLevel2Filename);
        zipLevel2StoreRef = dataStore.store(zipLevel2Stream, storeOutputLocation);
        InputStream zipLevel2DocStream = classLoader.getResourceAsStream(zipLevel2DocFilename);
        zipLevel2DocStoreRef = dataStore.store(zipLevel2DocStream, storeOutputLocation);
        InputStream zipLevel2DocContentStream = classLoader.getResourceAsStream(zipLevel2DocFilename +".content");
        zipLevel2DocContentRef = dataStore.store(zipLevel2DocContentStream, storeOutputLocation);
    }

    private void storeDocxData(ClassLoader classLoader) throws DataStoreException {
        InputStream docxStream = classLoader.getResourceAsStream("input/Good team.docx");
        docxStoreRef = dataStore.store(docxStream, storeOutputLocation);
        InputStream docxContentStream = classLoader.getResourceAsStream("input/Good team.docx.content");
        docxContentStoreRef = dataStore.store(docxContentStream, storeOutputLocation);
        testFieldRef = dataStore.store("this is the test data".getBytes(), storeOutputLocation);
    }

    private void storeJpgData(ClassLoader classLoader) throws DataStoreException {
        InputStream jpgStream = classLoader.getResourceAsStream("input/other - team lineup.jpg");
        jpgStoreRef = dataStore.store(jpgStream, storeOutputLocation);
        InputStream jpgContentStream = classLoader.getResourceAsStream("input/other - team lineup.jpg.content");
        jpgContentStoreRef = dataStore.store(jpgContentStream, storeOutputLocation);
    }

    public SharedDocument getJpgExample(String docRef){
        SharedDocument document = new SharedDocument();
        document.setReference(docRef);
        Collection<Map.Entry<String, String>> metadata = getJpgMetadata(docRef);
        document.getMetadata().addAll(metadata);
        Collection<Map.Entry<String, ReferencedData>> metadataReferences =
                getMetadataReferences(jpgContentStoreRef);

        document.getMetadataReference().addAll(metadataReferences);
        document.setDocumentProcessingRecord(generateDocumentProcessingRecord(metadata, metadataReferences));
        return document;
    }

    public SharedDocument getDocxExample(String docRef){
        SharedDocument document = new SharedDocument();
        document.setReference(docRef);
        Collection<Map.Entry<String, String>> metadata = getDocxMetadata();
        document.getMetadata().addAll(metadata);
        Collection<Map.Entry<String, ReferencedData>> metadataReferences =
                getDocxMetadataReferences(docxContentStoreRef, testFieldRef);

        document.getMetadataReference().addAll(metadataReferences);
        document.setDocumentProcessingRecord(generateDocumentProcessingRecord(metadata, metadataReferences));
        return document;
    }

    private Collection<Map.Entry<String, String>> getDocxMetadata(){
        Collection<Map.Entry<String, String>> docxMetadata = new ArrayList<>();
        docxMetadata.add(createMapEntry("appname", "Microsoft Office Word"));
        docxMetadata.add(createMapEntry("DOC_CLASS_CODE", "1"));
        docxMetadata.add(createMapEntry("FILTER_STATUS", "COMPLETED"));
        docxMetadata.add(createMapEntry("storageReference", docxStoreRef));
        docxMetadata.add(createMapEntry("title", "/input/Good team"));
        docxMetadata.add(createMapEntry("wordcount", "134"));
        return docxMetadata;
    }

    private SharedDocument createDocument(Collection<Map.Entry<String, String>> metadata, String docRef){
        return createDocument(metadata, docRef, null);
    }

    private SharedDocument createDocument(Collection<Map.Entry<String, String>> metadata, String docRef, String contentRef){
        SharedDocument document = new SharedDocument();
        document.setReference(docRef);
        document.getMetadata().addAll(metadata);
        Collection<Map.Entry<String, ReferencedData>> metadataReferences = null;
        if(contentRef!=null) {
            metadataReferences =
                    getMetadataReferences(contentRef);
            document.getMetadataReference().addAll(metadataReferences);
        }
        document.setDocumentProcessingRecord(generateDocumentProcessingRecord(metadata, metadataReferences));
        return document;
    }

    private static Collection<Map.Entry<String, ReferencedData>> getMetadataReferences(String contentRef){
        Collection<Map.Entry<String, ReferencedData>> metadataRefs = new ArrayList<>();
        metadataRefs.add(createMapEntry("CONTENT", ReferencedData.getReferencedData(contentRef)));
        return metadataRefs;
    }

    private static Collection<Map.Entry<String, ReferencedData>> getDocxMetadataReferences(String contentReference,
                                                                                           String testFieldRef){
        Collection<Map.Entry<String, ReferencedData>> docxMetadataRefs = new ArrayList<>();
        docxMetadataRefs.add(createMapEntry("CONTENT", ReferencedData.getReferencedData(contentReference)));
        docxMetadataRefs.add(createMapEntry("TEST_FIELD", ReferencedData.getReferencedData(testFieldRef)));
        return docxMetadataRefs;
    }

    private Collection<Map.Entry<String, String>> getJpgMetadata(String reference){
        Collection<Map.Entry<String, String>> metadata = new ArrayList<>();
        metadata.add(createMapEntry("creation software", "Adobe Photoshop 7.0"));
        metadata.add(createMapEntry("DOC_CLASS_CODE", "4"));
        metadata.add(createMapEntry("DOC_FORMAT_CODE", "143"));
        metadata.add(createMapEntry("FILENAME", "other - team lineup.jpg"));
        metadata.add(createMapEntry("FILTER_STATUS", "COMPLETED"));
        metadata.add(createMapEntry("height (pixels)", "480"));
        metadata.add(createMapEntry("MIME_TYPE", "application/jpeg"));
        metadata.add(createMapEntry("reference", reference));
        metadata.add(createMapEntry("storageReference", jpgStoreRef));
        return metadata;
    }

    public SharedDocument getZipLevel1DocExample(){
        Collection<Map.Entry<String, String>> metadata = getZipLevel1DocMetadata();
        return createDocument(metadata, "/input/Level1of11.zip:0", zipLevel1DocContentRef);
    }

    private Collection<Map.Entry<String, String>> getZipLevel1DocMetadata(){
        Collection<Map.Entry<String, String>> metadata = new ArrayList<>();
        metadata.add(createMapEntry("reference", "/input/Level1of11.zip:0"));
        metadata.add(createMapEntry("DOC_CLASS_CODE", "1"));
        metadata.add(createMapEntry("DOC_FORMAT_CODE", "360"));
        metadata.add(createMapEntry("storageReference", zipLevel1DocStoreRef));
        metadata.add(createMapEntry("TITLE", "Level1.docx"));
        metadata.add(createMapEntry("IMPORTMAGICEXTENSION", "docx"));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILENAME", "Level1.docx"));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILESIZE_BYTES", "11486"));
        metadata.add(createMapEntry("MIME_TYPE", "application/msword"));
        return metadata;
    }

    public SharedDocument getZipLevel2Example(){
        Collection<Map.Entry<String, String>> metadata = getZipLevel2Metadata();
        return createDocument(metadata, "/input/Level1of11.zip:1");
    }

    private Collection<Map.Entry<String, String>> getZipLevel2Metadata(){
        Collection<Map.Entry<String, String>> metadata = new ArrayList<>();
        metadata.add(createMapEntry("reference", "/input/Level1of11.zip:1"));
        metadata.add(createMapEntry("MIME_TYPE", "application/x-compressed"));
        metadata.add(createMapEntry("DOC_CLASS_CODE", "8"));
        metadata.add(createMapEntry("storageReference", zipLevel2StoreRef));
        metadata.add(createMapEntry("IMPORTMAGICEXTENSION", "zip"));
        metadata.add(createMapEntry("TITLE", "Level2.zip"));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILENAME", "Level2.zip"));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILESIZE_BYTES", "11486"));
        return metadata;
    }

    public SharedDocument getLevel2DocExample(){
        Collection<Map.Entry<String, String>> metadata = getZipLevel2DocMetadata();
        return createDocument(metadata, "/input/Level1of11.zip:1:0", zipLevel2DocContentRef);
    }

    private Collection<Map.Entry<String, String>> getZipLevel2DocMetadata(){
        Collection<Map.Entry<String, String>> metadata = new ArrayList<>();
        metadata.add(createMapEntry("TITLE", "Level2.docx"));
        metadata.add(createMapEntry("IMPORTMAGICEXTENSION", "docx"));
        metadata.add(createMapEntry("reference", "/input/Level1of11.zip:1:0"));
        metadata.add(createMapEntry("storageReference", zipLevel2DocStoreRef));
        metadata.add(createMapEntry("DOC_CLASS_CODE", "1"));
        metadata.add(createMapEntry("reference", zipFilename));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILENAME", "Level2.docx"));
        metadata.add(createMapEntry("KV_EXTRACTED_DOCUMENT_FILESIZE_BYTES", "11486"));
        return metadata;
    }

    public SharedDocument getZipRootExample(){
        Collection<Map.Entry<String, String>> metadata = getZipRootMetadata();
        return createDocument(metadata, "/"+zipFilename);
    }

    private Collection<Map.Entry<String, String>> getZipRootMetadata(){
        Collection<Map.Entry<String, String>> metadata = new ArrayList<>();
        metadata.add(createMapEntry("SUBFILE_COUNT", "2"));
        metadata.add(createMapEntry("PROCESSING_KeyviewWorker_VERSION", "7.3.0-12"));
        metadata.add(createMapEntry("MIME_TYPE", "application/x-compressed"));
        metadata.add(createMapEntry("storageReference", zipStoreRef));
        metadata.add(createMapEntry("DOC_CLASS_CODE", "8"));
        metadata.add(createMapEntry("reference", "/"+zipFilename));
        metadata.add(createMapEntry("EXTRACT_STATUS", "COMPLETED"));
        return metadata;
    }

    private static Map.Entry createMapEntry(String key, Object value){
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static DocumentProcessingRecord generateDocumentProcessingRecord(Collection<Map.Entry<String, String>> metadata,
                                                                             Collection<Map.Entry<String, ReferencedData>> metadataRefs
    ){
        Map<String, FieldChangeRecord> metadataChanges = new HashMap<>();
        for(Map.Entry<String, String> metadataEntry: metadata){
            FieldChangeRecord changeRecord = new FieldChangeRecord();
            changeRecord.changeType = FieldChangeType.added;
            FieldValue fieldValue = new FieldValue();
            fieldValue.value = metadataEntry.getValue();
            fieldValue.valueEncoding = FieldEncoding.utf8;
            changeRecord.changeValues = Arrays.asList(fieldValue);
            metadataChanges.put(metadataEntry.getKey(), changeRecord);
        }
        DocumentProcessingRecord record = new DocumentProcessingRecord();
        record.metadataChanges = metadataChanges;
        return record;
    }
}
