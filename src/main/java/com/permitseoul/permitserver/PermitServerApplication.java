package com.permitseoul.permitserver;

import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.domain.auth.core.external.google.GoogleProperties;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableConfigurationProperties({
		GoogleProperties.class,
		KakaoProperties.class,
		JwtProperties.class
})
@SpringBootApplication
public class PermitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermitServerApplication.class, args);
	}

}
