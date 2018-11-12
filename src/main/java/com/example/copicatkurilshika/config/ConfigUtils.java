package com.example.copicatkurilshika.config;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

public class ConfigUtils {

    public static ThreadPoolTaskExecutor threadPoolTaskExecutor(int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(coreSize);
        pool.setMaxPoolSize(maxSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setAllowCoreThreadTimeOut(true);
        return pool;
    }

    public static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(int maxTotalSize, int defaultMaxPerRoute, boolean rejectBadSsl) throws Exception {
        PoolingHttpClientConnectionManager cm;
        if (rejectBadSsl) {
            cm = new PoolingHttpClientConnectionManager();
        } else {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial((x509Certificates, s) -> true)
                    .build();
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
        }
        cm.setMaxTotal(maxTotalSize);
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return cm;
    }

    public static HttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                        long maxIdleConnectionTime) {
        return HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .evictExpiredConnections()
                .evictIdleConnections(maxIdleConnectionTime, TimeUnit.MILLISECONDS)
                .disableConnectionState()
                .disableAutomaticRetries()
                .build();
    }

    public static FutureRequestExecutionService futureRequestExecutionService(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                                                              HttpClient httpClient) {
        return new FutureRequestExecutionService(httpClient, threadPoolTaskExecutor.getThreadPoolExecutor());
    }

}
