package com.permitseoul.permitserver.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeLogger {

    @Around("@annotation(com.permitseoul.permitserver.global.aop.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // 메서드 실행

        final long end = System.currentTimeMillis();
        final String methodName = joinPoint.getSignature().toShortString();

        log.info("⏱️ [{}] 실행 시간: {} ms", methodName, (end - start));
        return result;
    }
}