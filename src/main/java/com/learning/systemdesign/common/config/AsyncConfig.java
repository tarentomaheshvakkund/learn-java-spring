package com.learning.systemdesign.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * MODULE 4: ASYNC CONFIGURATION
 * <p>
 * This class enables Spring's @Async annotation and defines a Thread Pool.
 * meaningful thread names (e.g., "async-task-1") help with debugging.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Start with 2 threads
        executor.setMaxPoolSize(5);  // Max 5 threads
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-task-");
        executor.initialize();
        return executor;
    }
}
