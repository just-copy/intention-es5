package com.justcp.es5.service.serviceImpl;

import com.justcp.es5.config.EsBulkProcessorConfig;
import com.justcp.es5.service.EsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Slf4j
@Service("esService")
public class EsServiceImpl implements EsService {

    @Resource
    private TransportClient client;

    @Override
    public void insertBatch() {

        BulkProcessor bulkProcessor = EsBulkProcessorConfig.bulkProcessor(client);
        int count = 0;
        while ( count <= 10000000) {
            count ++;
            XContentBuilder xContentBuilder = null;
            try {
                xContentBuilder = jsonBuilder()
                        .startObject()
                        .field("account", "account" + count)
                        .field("path", "path" + count)
                        .field("card", "card" + count)
                        .field("mid", "mid" + count)
                        .field("pid", "pid" + count)
                        .field("request_time", new Date())
                        .field("status", "status" + count)
                        .field("instanceId",count)
                        .endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bulkProcessor.add(new IndexRequest("intention_dev","query",count+"_1")
                    .source(xContentBuilder));
            bulkProcessor.add(new IndexRequest("intention_dev","query",count+"_2")
                    .source(xContentBuilder));
            bulkProcessor.add(new IndexRequest("intention_dev","query",count+"_3")
                    .source(xContentBuilder));
            if (count % 5000 == 0) {
                log.info(count / 5000 + "次");
            }
        }
        bulkProcessor.flush();
        try {
            bulkProcessor.awaitClose(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.out.println("关闭异常");
        }

    }
}
