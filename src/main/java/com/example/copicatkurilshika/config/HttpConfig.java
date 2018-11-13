package com.example.copicatkurilshika.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HttpConfig {

    @Value("${maxIdleConnectionTime}")
    private long maxIdleConnectionTime;
    @Value("${keepAliveSeconds}")
    private int keepAliveSeconds;
    @Value("${queueCapacity}")
    private int queueCapacity;

    @Value("${deliveredcorePoolSize}")
    private int deliveredCorePoolSize;
    @Value("${deliveredpoolSize}")
    private int deliveredMaxPoolSize;

    @Value("${seenCorePoolSize}")
    private int seenCorePoolSize;
    @Value("${seenMaxPoolSize}")
    private int seenMaxPoolSize;

    @Bean("deliveredFutureRequestExecutionService")
    public FutureRequestExecutionService deliveredExecutionService() throws Exception {
        return getFutureRequestExecutionService(deliveredCorePoolSize, deliveredMaxPoolSize);
    }

    @Bean("seenFutureRequestExecutionService")
    public FutureRequestExecutionService seenExecutionService() throws Exception {
        return getFutureRequestExecutionService(seenCorePoolSize, seenMaxPoolSize);
    }

    private FutureRequestExecutionService getFutureRequestExecutionService(int corePoolSize, int maxPoolSize) throws Exception {
        ThreadPoolTaskExecutor threadPool = ConfigUtils.threadPoolTaskExecutor(
                corePoolSize,
                maxPoolSize,
                queueCapacity,
                keepAliveSeconds);
        threadPool.initialize();

        PoolingHttpClientConnectionManager pollingManager = ConfigUtils.poolingHttpClientConnectionManager(
                corePoolSize,
                maxPoolSize,
                false);

        HttpClient httpClient = ConfigUtils.httpClient(pollingManager, maxIdleConnectionTime);
        return ConfigUtils.futureRequestExecutionService(threadPool, httpClient);
    }
}



