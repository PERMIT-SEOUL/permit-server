package com.permitseoul.permitserver.global.domain;

import com.permitseoul.permitserver.global.Constants;
import lombok.Getter;

@Getter
public enum CaffeineCacheType {
    REFRESH_TOKEN(Constants.REFRESH_TOKEN, 30);

    private static final int MAXIMUM_CACHE_SIZE = 20000;

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;

    CaffeineCacheType(String cacheName, int expireAfterWrite) {
        this.cacheName = cacheName;
        this.expireAfterWrite = expireAfterWrite;
        this.maximumSize = MAXIMUM_CACHE_SIZE;
    }
}
