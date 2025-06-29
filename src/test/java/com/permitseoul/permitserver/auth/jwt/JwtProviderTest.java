package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.user.domain.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private JwtGenerator jwtGenerator;
    private JwtProvider jwtProvider;
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        String secret = "ThisIsASecretKeyForJwtGeneration1234567890";
        JwtProperties jwtProperties = new JwtProperties(secret, 1000 * 60 * 15, 1000 * 60 * 60 * 24 * 7);
        jwtGenerator = new JwtGenerator(jwtProperties);
        jwtProvider = new JwtProvider(jwtGenerator, cacheManager);
    }

    @Test
    void 토큰_발급_및_userId_정상_추출() {
        // given
        long userId = 123L;
        UserRole userRole = UserRole.USER;

        // when
        Token token = jwtProvider.issueToken(userId, userRole);
        long extractedUserId = jwtProvider.extractUserIdFromToken(token.getAccessToken());

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void 잘못된_토큰_주면_예외_발생() {
        // given
        String invalidToken = "abc.def.ghi";

        // expect
        assertThatThrownBy(() -> jwtProvider.extractUserIdFromToken(invalidToken))
                .isInstanceOf(AuthWrongJwtException.class);
    }

    @Test
    void 만료된_토큰_파싱시_AuthExpiredJwtException_발생() {
        // given
        long userId = 1L;
        String expiredToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
                .setExpiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(jwtGenerator.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        // when & then
        Assertions.assertThrows(AuthExpiredJwtException.class, () -> {
            jwtProvider.extractUserIdFromToken(expiredToken);
        });
    }

}