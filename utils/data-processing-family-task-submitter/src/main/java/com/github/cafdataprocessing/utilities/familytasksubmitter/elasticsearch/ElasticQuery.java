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
package com.github.cafdataprocessing.utilities.familytasksubmitter.elasticsearch;

import com.github.cafdataprocessing.utilities.familytasksubmitter.FamilyTaskSubmitterConstants;
import com.hpe.caf.api.ConfigurationException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mcgreeva
 */
public class ElasticQuery
{
    private final static Logger LOG = LoggerFactory.getLogger(ElasticQuery.class);

    //Refer to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-from-size.html
    private final static int ELASTIC_INDEX_MAX_RESULT_WINDOW = 10000;

    private final String elasticClusterName = getEnvironmentValue(FamilyTaskSubmitterConstants.Elastic.CAF_ELASTIC_CLUSTERNAME,
                                                                    "docker-cluster");
    private final String elasticIndexName = getEnvironmentValue(FamilyTaskSubmitterConstants.Elastic.CAF_ELASTIC_INDEX_NAME,
                                                                  "data-processing");
    private final String elasticType = getEnvironmentValue(FamilyTaskSubmitterConstants.Elastic.CAF_ELASTIC_TYPE, "document");
    private final String elasticHostName = getEnvironmentValue(FamilyTaskSubmitterConstants.Elastic.CAF_ELASTIC_HOSTNAME, "localhost");
    private final int elasticPort = Integer.parseInt(getEnvironmentValue(FamilyTaskSubmitterConstants.Elastic.CAF_ELASTIC_TRANSPORT_PORT,
                                                                          "9300"));
    private final TransportClient transportClient;
    private final String familyReference;

    public ElasticQuery(final String familyReference) throws ConfigurationException
    {
        this.transportClient = getTransportClient();
        this.familyReference = familyReference;
    }

    public Map<String, Object> createFamily(final String filename)
    {
        try {
            final Map<String, Object> fileMap = new HashMap<>();
            fileMap.put(filename, getHits(issueGet(familyReference)));
            return fileMap;
        } catch (JSONException ex) {
            LOG.error("Failed to convert search result into JSON: ", ex);
            throw ex;
        }
    }

    private SearchResponse issueGet(final String familyRef)
    {
        return transportClient.prepareSearch(elasticIndexName)
            .setTypes(elasticType)
            .setSearchType(SearchType.QUERY_THEN_FETCH)
            .setFetchSource(true)
            .setSize(ELASTIC_INDEX_MAX_RESULT_WINDOW)
            .setQuery(boolQuery()
                .must(queryStringQuery("(FAMILY_reference:\"" + familyRef + "\")")))
            .execute()
            .actionGet();
    }

    private TransportClient getTransportClient() throws ConfigurationException
    {
        final TransportClient client;
        try {
            client = new PreBuiltTransportClient(
                Settings.builder()
                    .put("client.transport.ping_timeout", "15s")
                    .put("client.transport.sniff", false)
                    .put("client.transport.nodes_sampler_interval", "15s")
                    .put("cluster.name", elasticClusterName)
                    .build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHostName), elasticPort));
        } catch (UnknownHostException e) {
            throw new ConfigurationException(String.format("Unknown host \"[%s]\" for Elasticsearch", elasticHostName), e);
        }
        return client;
    }

    private Map<String, Object> getHits(final SearchResponse result)
    {
        final Map<String, Object> hits = new HashMap<>();
        result.getHits().forEach(hit -> addHit(hits, hit));
        return hits;
    }

    private static String getEnvironmentValue(final String name, final String defaultValue)
    {
        return System.getProperty(name, System.getenv(name) != null ? System.getenv(name) : defaultValue);
    }

    private void addHit(final Map<String, Object> map, final SearchHit hit)
    {
        final Map<String, Object> hitFields = getHitFields(hit);
        final List<String> hitReference = (List<String>) hitFields.get("reference");
        map.put(hitReference.iterator().next(), hitFields);
    }

    private Map<String, Object> getHitFields(final SearchHit hit)
    {
        final Map<String, Object> hitFields = new HashMap<>();
        hit.getSource().forEach(hitFields::put);
        return hitFields;
    }
}
