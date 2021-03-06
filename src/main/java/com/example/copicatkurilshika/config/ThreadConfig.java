package com.example.copicatkurilshika.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
@Configuration
@EnableAsync
public class ThreadConfig {

    @Value("${corePoolSize}")
    private int corePoolSize;
    @Value("${maxPoolSize}")
    private int maxPoolSize;
    @Value("${keepAliveSeconds}")
    private int keepAlive;
    @Value("${threadNamePrefix}")
    private String threadNamePrefix;

    @Bean(name = "viberStatusSenderTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setThreadPriority(7);
        executor.initialize();
        return executor;
    }

}
