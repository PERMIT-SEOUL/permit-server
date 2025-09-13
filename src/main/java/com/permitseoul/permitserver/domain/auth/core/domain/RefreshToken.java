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

    /** 사용자 식별자(키). 사용자당 1개 세션만 허용 모델 */
    @Id
    private Long userId;

    /** 토큰 해시(SHA-256 등). 평문 저장 금지 */
    private String refreshToken;

    /** 만료 시간(초 단위). @RedisHash 클래스 단위 TTL 대신 인스턴스별 TTL 추천 */
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