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

import com.github.cafdataprocessing.corepolicy.multimap.utils.CaseInsensitiveMultimap;
import com.google.common.collect.Multimap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mcgreeva
 */
public class DocumentExtractor
{
    private final Map<String, Object> family;
    private final Map<String, Multimap<String, String>> documents = new HashMap<>();

    public DocumentExtractor(final Map<String, Object> family)
    {
        this.family = family;
    }

    public Map<String, Multimap<String, String>> convert()
    {
        family.keySet().forEach((document) -> {
            HashMap<String, HashMap> docs = (HashMap) family.get(document);
            docs.keySet().stream().map((key) -> {
                docs.get(key);
                return key;
            }).forEachOrdered((key) -> {
                documents.put(key, convertDocumentMetadata(docs.get(key)));
            });
        });
        return documents;
    }

    private Multimap<String, String> convertDocumentMetadata(final Map<String, List<String>> fieldsAndValues)
    {
        Multimap<String, String> map = CaseInsensitiveMultimap.create();
        fieldsAndValues.keySet().forEach((field) -> {
            List<String> values = (List) fieldsAndValues.get(field);
            values.forEach((value) -> {
                map.put(field, value);
            });
        });
        return map;
    }
}
