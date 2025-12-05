package com.permitseoul.permitserver;

import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.global.external.google.GoogleProperties;
import com.permitseoul.permitserver.global.external.kakao.KakaoProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableConfigurationProperties({
		GoogleProperties.class,
		KakaoProperties.class,
		JwtProperties.class
})
@SpringBootTest
class PermitServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
