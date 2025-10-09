package com.permitseoul.permitserver.global.config;


import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients("com.permitseoul")
public class FeignConfig {
    @Bean
    public Retryer retryer() {
        // 3회 재시도 (최대 1초 간격)
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }
}
