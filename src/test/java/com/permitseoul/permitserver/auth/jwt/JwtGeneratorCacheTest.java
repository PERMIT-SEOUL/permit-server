package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.user.domain.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtGeneratorCacheTest {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private CacheManager cacheManager;

    //테스트 후 캐시 삭제
    @AfterEach
    void tearDown() {
        cacheManager.getCache(Constants.REFRESH_TOKEN).clear();
    }

    @Test
    void 리프레시_토큰_캐시에_정상_저장됨() {
        // given
        long userId = 1L;

        // when
        String token = jwtGenerator.generateRefreshToken(userId, UserRole.USER);

        // then
        String cachedToken = cacheManager.getCache(Constants.REFRESH_TOKEN)
                .get(userId, String.class);

        assertThat(cachedToken).isEqualTo(token);
    }
}