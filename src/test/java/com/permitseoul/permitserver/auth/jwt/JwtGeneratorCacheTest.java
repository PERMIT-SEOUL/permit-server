package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.user.domain.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtGeneratorCacheTest {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JwtProvider jwtProvider;

    //테스트 후 캐시 삭제
    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache(Constants.REFRESH_TOKEN)).clear();
    }

    @Test
    void 리프레시_토큰_캐시에_정상_저장됨() {
        // given
        long userId = 1L;

        // when
        String token = jwtGenerator.generateRefreshToken(userId, UserRole.USER);

        // then
        String cachedToken = jwtProvider.getRefreshTokenFromCache(userId);

        assertThat(cachedToken).isEqualTo(token);
    }

    @Test
    void 캐시에_userId_값이_없으면_null_반환() {
        // given
        long nonExistUserId = 999L;

        // when
        String token = jwtProvider.getRefreshTokenFromCache(nonExistUserId);

        // then
        Assertions.assertNull(token);
    }




}