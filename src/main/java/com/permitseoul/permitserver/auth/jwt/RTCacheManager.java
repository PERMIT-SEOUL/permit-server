package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.exception.AuthRTCacheException;
import com.permitseoul.permitserver.global.Constants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class RTCacheManager {

    //RT 캐시에서 삭제
    @CacheEvict(value = Constants.REFRESH_TOKEN, key = "#userId")
    public void deleteRefreshToken(final long userId) { }

    //RT 캐시에서 조회
    @Cacheable(value = Constants.REFRESH_TOKEN, key = "#userId")
    public String findRTFromCache(final long userId) {
        throw new AuthRTCacheException(); ///아무 값이 안들어가있으면 예외
    }
}
