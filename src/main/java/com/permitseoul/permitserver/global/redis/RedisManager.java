package com.permitseoul.permitserver.global.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class RedisManager {

    private final StringRedisTemplate redisTemplate;

    public void set(final String key,
                    final String value,
                    final Duration ttl) {
        if (ttl != null) {
            redisTemplate.opsForValue().set(key, value, ttl);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
        log.debug("[RedisService] SET key={}, value={}, ttl={}", key, value, ttl);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
        log.debug("[RedisService] DELETE key={}", key);
    }

    public boolean isExists(final String key) {
        final Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    public Long decrement(final String key, final long count) {
        return redisTemplate.opsForValue().decrement(key, count);
    }

    public Long increment(final String key, final long count) {
        return redisTemplate.opsForValue().increment(key, count);
    }
}