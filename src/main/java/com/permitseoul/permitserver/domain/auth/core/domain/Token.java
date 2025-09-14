package com.permitseoul.permitserver.domain.auth.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Token {

    private String accessToken;

    private String refreshToken;

    public static Token of(
            final String accessToken,
            final String refreshToken
    ) {
        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
