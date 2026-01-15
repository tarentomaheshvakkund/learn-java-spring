package com.learning.systemdesign.module6.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MODULE 6: CACHING CONFIGURATION
 * <p>
 * Enables Spring's Caching abstraction.
 * We use ConcurrentMapCacheManager (In-Memory) for simplicity.
 * In production, you would swap this for RedisCacheManager.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users");
    }
}
