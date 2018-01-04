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
package com.github.cafdataprocessing.utilities.familytasksubmitter.document;

import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterConstants;
import com.google.common.collect.Multimap;
import com.hpe.caf.worker.document.DocumentWorkerDocument;
import com.hpe.caf.worker.document.DocumentWorkerFieldEncoding;
import com.hpe.caf.worker.document.DocumentWorkerFieldValue;
import org.elasticsearch.common.Strings;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HierarchyBuilder
{
    private Map<String, Multimap<String, String>> documentsMetadataMap;

    public DocumentWorkerDocument convert(final Map<String, Multimap<String, String>> documentsMetadataMap,
                                          final String familyReference)
    {
        this.documentsMetadataMap = documentsMetadataMap;
        return getRootDocument(familyReference);
    }

    private DocumentWorkerDocument getRootDocument(final String familyReference)
    {
        for (String document : documentsMetadataMap.keySet()) {
            final Multimap<String, String> documentMap = documentsMetadataMap.get(document);
            if (isFamilyRoot(documentMap, familyReference)) {
                List<DocumentWorkerDocument> subfiles = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(documentMap.get(FamilyTaskSubmitterConstants.CHILD_INFO_COUNT).stream().findFirst()
                     .orElse("0")); i++) {
                    subfiles.add(getSubDocument(i, document));
                }
                return createDocument(document, documentMap, subfiles);
            }
        }
        throw new UnsupportedOperationException();
    }

    private DocumentWorkerDocument getSubDocument(final int childOrdinal, final String parentReference) {
        final String subDocReference = parentReference + ":" + childOrdinal;
        return getSubDocument(subDocReference);
    }

    private DocumentWorkerDocument getSubDocument(final String subDocReference)
    {
        final Multimap<String, String> subDocumentMap = documentsMetadataMap.get(subDocReference);
        List<DocumentWorkerDocument> subfiles = new ArrayList<>();

        //get the sub files of this sub document
        for(Map.Entry<String, Multimap<String, String>> documentEntry: this.documentsMetadataMap.entrySet()){
            Multimap<String, String> documentMap = documentEntry.getValue();
            if(documentMap.get("PARENT_REFERENCE") != null && !documentMap.get("PARENT_REFERENCE").isEmpty()
                    && documentMap.get("PARENT_REFERENCE").iterator().next().equals(subDocReference)){
                subfiles.add(getSubDocument(documentEntry.getKey()));
            }
        }
        return createDocument(subDocReference, subDocumentMap, subfiles);
    }

    private DocumentWorkerDocument createDocument(final String reference, final Multimap<String, String> providedDocumentMetadata,
                                    final List<DocumentWorkerDocument> subfiles)
    {
        DocumentWorkerDocument document = new DocumentWorkerDocument();
        // send the CONTENT field as a base64 encoded field value so we can verify metadata references are handled correctly by
        // workers operating on a family
        String contentFieldName = System.getProperty(FamilyTaskSubmitterConstants.CONTENT_FIELD_NAME, System.getenv(FamilyTaskSubmitterConstants.CONTENT_FIELD_NAME));
        if (Strings.isNullOrEmpty(contentFieldName)) {
            contentFieldName = FamilyTaskSubmitterConstants.DEFAULT_CONTENT_FIELD_NAME;
        }
        Map<String, List<DocumentWorkerFieldValue>> docFieldsToSend = new HashMap<>();
        for(Map.Entry<String, String> docMetadataEntry: providedDocumentMetadata.entries()){
            String metadataKey = docMetadataEntry.getKey();
            DocumentWorkerFieldValue metadataFieldValue = new DocumentWorkerFieldValue();
            if(metadataKey.equals(contentFieldName)){
                byte[] dataBytesValue = docMetadataEntry.getValue().getBytes();
                metadataFieldValue.encoding = DocumentWorkerFieldEncoding.base64;
                metadataFieldValue.data = Base64.getEncoder().encodeToString(dataBytesValue);
            }
            else {
                String dataStrValue = docMetadataEntry.getValue();
                metadataFieldValue.encoding = DocumentWorkerFieldEncoding.utf8;
                metadataFieldValue.data = dataStrValue;
            }
            if(docFieldsToSend.containsKey(metadataKey)){
                docFieldsToSend.get(metadataKey).add(metadataFieldValue);
            }
            else {
                List<DocumentWorkerFieldValue> fieldValues = new ArrayList<>();
                fieldValues.add(metadataFieldValue);
                docFieldsToSend.put(metadataKey, fieldValues);
            }
        }
        document.fields = docFieldsToSend;
        document.reference = reference;
        document.subdocuments = subfiles;
        return document;
    }

    private boolean isFamilyRoot(final Multimap<String, String> documentMap, final String familyReference)
    {
        final String reference = documentMap.get("DREREFERENCE").stream().findFirst().orElse("");
        return reference.equals(familyReference);
    }
}
