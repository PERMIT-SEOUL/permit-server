package com.permitseoul.permitserver.domain.auth.core.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;

    @TimeToLive
    private long ttlSeconds;

    public static RefreshToken of(final long userId, final String refreshToken, final long ttlSeconds) {
        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ttlSeconds(ttlSeconds)
                .build();
    }
}