package com.justcp.es5.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class EsClientConfig {

    @Value("${research.es5.es.servers}")
    private String esHostname;

    @Value("${research.es5.es.port}")
    private int port;

    @Value("${research.es5.es.cluster.name}")
    private String clusterName;

    @Bean
    public TransportClient getTransportClient() throws Exception{
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", false)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHostname), port));

        return client;
    }


}
