package com.justcp.es5.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class EsClientConfig {

    @Bean
    public TransportClient getTransportClient() throws Exception{
        Settings settings = Settings.builder()
                .put("cluster.name", "es_5.6.10")
                .put("client.transport.sniff", false)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.43.155"), 9300));

        return client;
    }


}
