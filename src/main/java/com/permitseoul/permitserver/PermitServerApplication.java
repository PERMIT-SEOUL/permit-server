package com.permitseoul.permitserver;

import com.permitseoul.permitserver.domain.admin.base.api.AdminProperties;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.domain.auth.core.external.google.GoogleProperties;
import com.permitseoul.permitserver.domain.auth.core.external.kakao.KakaoProperties;
import com.permitseoul.permitserver.domain.reservation.api.TossProperties;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.SessionProperties;
import com.permitseoul.permitserver.global.HashIdProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableConfigurationProperties({
		GoogleProperties.class,
		KakaoProperties.class,
		JwtProperties.class,
		TossProperties.class,
		HashIdProperties.class,
		SessionProperties.class,
		AdminProperties.class
})
@SpringBootApplication
public class PermitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermitServerApplication.class, args);
	}

}
