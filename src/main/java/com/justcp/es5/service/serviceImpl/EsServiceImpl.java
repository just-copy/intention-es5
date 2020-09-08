package com.justcp.es5.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.justcp.es5.config.EsBulkProcessorConfig;
import com.justcp.es5.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Slf4j
@Service("esService")
public class EsServiceImpl implements EsService {

    @Resource
    private TransportClient client;

    @Value("${research.es7.es.index.devIndex}")
    private String devIndex;

    @Value("${research.es7.es.type.devType}")
    private String devType;

    @Override
    public void insertBatch() {

        try {
            BulkProcessor bulkProcessor = EsBulkProcessorConfig.bulkProcessor(client);
            int count = 0;
            //把导出的结果以JSON的格式写到文件里
            for (int i = 0; i < 20; i++) {
                BufferedReader br = new BufferedReader(new FileReader("/Users/lilong/logs/log.json"));
                String json = null;
                while ((json = br.readLine()) != null) {
                    JSONObject jsonObject =JSONObject.parseObject(json);
                    jsonObject.put("instanceId", count / 1000 + 1);
                    bulkProcessor.add(new IndexRequest(devIndex, devType).source(jsonObject.toJSONString(), XContentType.JSON));
                    //每一千条提交一次
                    count++;
                }
                br.close();
            }

            bulkProcessor.flush();
            try {
                bulkProcessor.awaitClose(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteByQuery() {
        try {
            QueryBuilder qb = QueryBuilders.boolQuery()
                    .filter(QueryBuilders.typeQuery(devType))
                    .mustNot(QueryBuilders.existsQuery("instanceIds"));
            BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                    .source(devIndex)
                    .filter(qb)
                    .get();
            // 返回成功删除条数
            TimeValue timeTaken = response.getTook();
            boolean timedOut = response.isTimedOut();
            long totalDocs = response.getStatus().getTotal();
            long deletedDocs = response.getDeleted();
            long batches = response.getBatches();
            long noops = response.getNoops();
            log.info("timeTaken:" + timeTaken);
            log.info("timedOut:" + timedOut);
            log.info("totalDocs:" + totalDocs);
            log.info("deletedDocs:" + deletedDocs);
            log.info("batches:" + batches);
            log.info("noops:" + noops);
            log.info("timeTaken:" + timeTaken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
