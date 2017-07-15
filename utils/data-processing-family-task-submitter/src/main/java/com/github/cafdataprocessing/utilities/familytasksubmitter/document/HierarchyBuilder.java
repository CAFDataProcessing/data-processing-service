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
package com.github.cafdataprocessing.utilities.familytasksubmitter.document;

import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterConstants;
import com.google.common.collect.Multimap;
import com.hp.autonomy.policyworker.shared.Document;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class HierarchyBuilder
{
    private Map<String, Multimap<String, String>> documents;

    public HierarchyBuilder()
    {
    }

    public Document convert(final Map<String, Multimap<String, String>> documents, final String familyReference)
    {
        this.documents = documents;
        return getRootDocument(familyReference);
    }

    private Document getRootDocument(final String familyReference)
    {
        for (String document : documents.keySet()) {
            final Multimap<String, String> documentMap = documents.get(document);
            if (isFamilyRoot(documentMap, familyReference)) {
                Collection<Document> subfiles = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(documentMap.get(FamilyTaskSubmitterConstants.CHILD_INFO_COUNT).stream().findFirst()
                     .orElse("0")); i++) {
                    subfiles.add(getSubDocument(i, document));
                }
                return createDocument(document, documentMap, subfiles);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Document getSubDocument(final int childOrdinal, final String parentReference) {
        final String subDocReference = parentReference + ":" + childOrdinal;
        return getSubDocument(subDocReference);
    }

    private Document getSubDocument(final String subDocReference)
    {
        final Multimap<String, String> subDocumentMap = documents.get(subDocReference);
        Collection<Document> subfiles = new ArrayList<>();

        //get the sub files of this sub document
        for(Map.Entry<String, Multimap<String, String>> documentEntry: this.documents.entrySet()){
            Multimap<String, String> documentMap = documentEntry.getValue();
            if(documentMap.get("PARENT_REFERENCE") != null && !documentMap.get("PARENT_REFERENCE").isEmpty()
                    && documentMap.get("PARENT_REFERENCE").iterator().next().equals(subDocReference)){
                subfiles.add(getSubDocument(documentEntry.getKey()));
            }
        }
        return createDocument(subDocReference, subDocumentMap, subfiles);
    }

    private Document createDocument(final String reference, final Multimap<String, String> documentMetadata,
                                    final Collection<Document> subfiles)
    {
        Document document = new Document();
        document.setDocuments(subfiles);
        document.setMetadata(documentMetadata);
        document.setReference(reference);
        document.setPolicyDataProcessingRecord(null);
        return document;
    }

    private boolean isFamilyOrigin(final Multimap<String, String> documentMap)
    {
        final int children = Integer.parseInt(documentMap.get(FamilyTaskSubmitterConstants.CHILD_INFO_COUNT).stream().findFirst().orElse("0"));
        return children > 0;
    }

    private boolean isFamilyRoot(final Multimap<String, String> documentMap, final String familyReference)
    {
        final String reference = documentMap.get("DREREFERENCE").stream().findFirst().orElse("");
        return reference.equals(familyReference);
    }
}
