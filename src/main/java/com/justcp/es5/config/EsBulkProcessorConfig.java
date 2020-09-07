package com.justcp.es5.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class EsBulkProcessorConfig {

    public static BulkProcessor bulkProcessor(TransportClient client){

        return BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                int i = request.numberOfActions();
                log.info("批量导入数据量：" + i);

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                //在这儿你可以自定义执行完同步之后执行什么
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
//                log.error("写入ES 重新消费");
            }
        }).setBulkActions(2000) //  达到刷新的条数
                .setBulkSize(new ByteSizeValue(3, ByteSizeUnit.MB)) // 达到 刷新的大小
                .setFlushInterval(TimeValue.timeValueSeconds(5)) // 固定刷新的时间频率
                .setConcurrentRequests(1) //并发线程数
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) // 重试补偿策略
                .build();

    }

}
