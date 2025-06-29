package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.exception.AuthRTCacheException;
import com.permitseoul.permitserver.global.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RTCacheManager {
    private final CacheManager cacheManager;

    //RT 캐시에서 삭제
    public void deleteRefreshTokenFromCache(final long userId) {
        final Cache cache = cacheManager.getCache(Constants.REFRESH_TOKEN);
        if (cache == null) {
            throw new AuthRTCacheException();
        }
        cache.evict(userId);
    }

    //RT 캐시에서 조회
    public String getRefreshTokenFromCache(final long userId) {
        final Cache cache = cacheManager.getCache(Constants.REFRESH_TOKEN);
        if (cache == null) {
            throw new AuthRTCacheException();
        }
        return cache.get(userId, String.class);
    }
}
