package com.example.copicatkurilshika.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

@Configuration
public class HttpConfig {

    @Value("${maxIdleConnectionTime}")
    private long maxIdleConnectionTime;
    @Value("${keepAliveSeconds}")
    private int keepAliveSeconds;

    @Value("${deliveredcorePoolSize}")
    private int deliveredCorePoolSize;
    @Value("${deliveredpoolSize}")
    private int deliveredMaxPoolSize;
    @Value("${deliverQueueCapacity}")
    private int deliverQueueCapacity;

    @Value("${seenCorePoolSize}")
    private int seenCorePoolSize;
    @Value("${seenMaxPoolSize}")
    private int seenMaxPoolSize;
    @Value("${seenQueueCapacity}")
    private int seenQueueCapacity;

    @Bean("deliveredFutureRequestExecutionService")
    public FutureRequestExecutionService deliveredExecutionService() throws Exception {
        return getFutureRequestExecutionService(deliveredCorePoolSize, deliveredMaxPoolSize, deliverQueueCapacity, MAX_PRIORITY);
    }

    @Bean("seenFutureRequestExecutionService")
    public FutureRequestExecutionService seenExecutionService() throws Exception {
        return getFutureRequestExecutionService(seenCorePoolSize, seenMaxPoolSize, seenQueueCapacity, MIN_PRIORITY);
    }

    private FutureRequestExecutionService getFutureRequestExecutionService(int corePoolSize, int maxPoolSize, int queueCapacity, int priority) throws Exception {
        ThreadPoolTaskExecutor threadPool = ConfigUtils.threadPoolTaskExecutor(
                corePoolSize,
                maxPoolSize,
                queueCapacity,
                keepAliveSeconds,
                priority);
        threadPool.initialize();

        PoolingHttpClientConnectionManager pollingManager = ConfigUtils.poolingHttpClientConnectionManager(
                corePoolSize,
                maxPoolSize,
                false);

        HttpClient httpClient = ConfigUtils.httpClient(pollingManager, maxIdleConnectionTime);
        return ConfigUtils.futureRequestExecutionService(threadPool, httpClient);
    }
}



