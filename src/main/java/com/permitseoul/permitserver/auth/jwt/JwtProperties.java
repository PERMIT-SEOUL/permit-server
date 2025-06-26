package com.permitseoul.permitserver.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationTime,
        long refreshTokenExpirationTime
) {
}
