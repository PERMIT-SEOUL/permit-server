package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private JwtGenerator jwtGenerator;
    private JwtProvider jwtProvider;



    @BeforeEach
    void setUp() {
        String secret = "ThisIsASecretKeyForJwtGeneration1234567890";
        JwtProperties jwtProperties = new JwtProperties(secret, 1000 * 60 * 15, 1000 * 60 * 60 * 24 * 7);
        jwtGenerator = new JwtGenerator(jwtProperties);
        jwtProvider = new JwtProvider(jwtGenerator);
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
    void 엑세스_토큰_만료_테스트() throws InterruptedException {
        // given
        String secret = "ThisIsASecretKeyForJwtGeneration1234567890";

        // 만료시간 1초 설정
        JwtProperties shortExpireProps = new JwtProperties(secret, 1000, 1000 * 60 * 60 * 24 * 7);

        JwtGenerator shortGenerator = new JwtGenerator(shortExpireProps);
        JwtProvider shortProvider = new JwtProvider(shortGenerator);

        String token = shortGenerator.generateAccessToken(1L, UserRole.USER);

        // 1.5초 대기 후 만료 유도
        Thread.sleep(1500);

        // expect: 만료 예외 발생
        assertThatThrownBy(() -> shortProvider.extractUserIdFromToken(token))
                .isInstanceOf(AuthExpiredJwtException.class);
    }

}