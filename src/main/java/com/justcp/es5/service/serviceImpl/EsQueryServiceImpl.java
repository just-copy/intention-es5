package com.justcp.es5.service.serviceImpl;

import com.justcp.es5.service.EsQueryService;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @description EsQueryServiceImpl
 */
@Slf4j
@Service("esQueryService")
public class EsQueryServiceImpl implements EsQueryService {

    @Resource
    private TransportClient client;

    @Value("${research.es5.es.index.devIndex}")
    private String devIndex;

    @Value("${research.es5.es.type.devType}")
    private String devType;

    @Override
    public void searchQuery() {
        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(devIndex);
            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
            searchRequestBuilder.setSize(10000);
            SearchResponse searchResponse = searchRequestBuilder.get();
            RestStatus status = searchResponse.status();
            long took = searchResponse.getTook().getMillis();
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            int length = hits.getHits().length;
            log.info("took:" + took + " value :" + totalHits + " length:" + length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
