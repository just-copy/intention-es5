package com.justcp.es5.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class EsBulkProcessorConfig {

    public static BulkProcessor bulkProcessor(TransportClient client){

        return BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                log.info("executionId: " + executionId + ", num to execute: " + request.numberOfActions());

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                Iterator<BulkItemResponse> iterator = response.iterator();
                long cntSuccess = 0,cntFail = 0,cntConflict = 0;
                while (iterator.hasNext()) {
                    BulkItemResponse BulkResponse = iterator.next();
                    switch (BulkResponse.status()) {
                        case OK:
                            cntSuccess ++;
                            break;
                        case CREATED:
                            cntSuccess ++;
                            break;
                        case ACCEPTED:
                            cntSuccess++;
                            break;
                        case CONFLICT:
                            cntConflict++;
                            break;
                        default:
                            cntFail++;
                    }
                }
                log.info("executionId: " + executionId + ", num success: " + cntSuccess +
                        ", num fail: " + cntFail +", num conflict: " + cntConflict);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("executionId: " + executionId + ", FAILURE MESSAGE: " + failure.getMessage());
            }
        }).setBulkActions(2000) //  达到刷新的条数
                .setBulkSize(new ByteSizeValue(3, ByteSizeUnit.MB)) // 达到 刷新的大小
                .setFlushInterval(TimeValue.timeValueSeconds(5)) // 固定刷新的时间频率
                .setConcurrentRequests(1) //并发线程数
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) // 重试补偿策略
                .build();

    }

}
