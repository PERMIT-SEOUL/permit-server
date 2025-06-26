package com.permitseoul.permit_server.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.permitseoul.permit_server.global.Constants;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        caches.add(new CaffeineCache(Constants.REFRESH_TOKEN, Caffeine.newBuilder()
                .expireAfterWrite(14, TimeUnit.DAYS) /// refresToken 만료기간인 14일과 같도록 설정
                .initialCapacity(100)
                .maximumSize(500)
                .recordStats()
                .build()));
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
