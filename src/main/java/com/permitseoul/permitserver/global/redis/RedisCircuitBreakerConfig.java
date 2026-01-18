package com.permitseoul.permitserver.global.redis;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisCircuitBreakerConfig {

    @Bean
    public CircuitBreaker redisCircuitBreaker(final CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("redis");
    }
}
