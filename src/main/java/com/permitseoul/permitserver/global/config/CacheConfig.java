package com.permitseoul.permitserver.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.domain.CaffeineCacheType;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches = Arrays.stream(CaffeineCacheType.values())
                .map(caffeineCacheType -> new CaffeineCache(
                        caffeineCacheType.getCacheName(),
                        Caffeine.newBuilder()
                                .expireAfterWrite(caffeineCacheType.getExpireAfterWrite(), TimeUnit.DAYS)
                                .maximumSize(caffeineCacheType.getMaximumSize())
                                .build()
                ))
                .toList();

        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
