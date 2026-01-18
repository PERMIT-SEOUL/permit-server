package com.permitseoul.permitserver.global.redis;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCircuitBreakerLogger {

    public RedisCircuitBreakerLogger(final CircuitBreakerRegistry registry) {
        final CircuitBreaker circuitBreaker = registry.circuitBreaker("redis");
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    final CircuitBreaker.State toState = event.getStateTransition().getToState();
                    if (toState == CircuitBreaker.State.OPEN || toState == CircuitBreaker.State.CLOSED) {
                        final CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
                        log.error(
                                "[RedisCircuitBreaker] transition={} at={} failureRate={} slowCallRate={} bufferedCalls={} failedCalls={} successfulCalls={} notPermittedCalls={}",
                                event.getStateTransition(),
                                event.getCreationTime(),
                                metrics.getFailureRate(),
                                metrics.getSlowCallRate(),
                                metrics.getNumberOfBufferedCalls(),
                                metrics.getNumberOfFailedCalls(),
                                metrics.getNumberOfSuccessfulCalls(),
                                metrics.getNumberOfNotPermittedCalls()
                        );
                    }
                });
    }
}
