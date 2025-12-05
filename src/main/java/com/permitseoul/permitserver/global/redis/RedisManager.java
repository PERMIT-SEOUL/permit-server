package com.permitseoul.permitserver.global.redis;

import com.permitseoul.permitserver.global.exception.RedisKeyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;


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
//        log.debug("[RedisManager] SET key={}, value={}, ttl={}", key, value, ttl);
    }

    public boolean setIfAbsent(final String key, final String value) {
        final Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, value);
        //        log.debug("[RedisManager] SET NX key={}, value={}, created={}", key, value, created);
        return Boolean.TRUE.equals(ok);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
//        log.debug("[RedisManager] DELETE key={}", key);
    }

    public boolean isExists(final String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public Long decrement(final String key, final long count) {
        if(!isExists(key)) {
            throw new RedisKeyNotFoundException();
        }
        return redisTemplate.opsForValue().decrement(key, count);
    }

    public Long increment(final String key, final long count) {
        if(!isExists(key)) {
            throw new RedisKeyNotFoundException();
        }
        return redisTemplate.opsForValue().increment(key, count);
    }

    public void mSet(final Map<String, String> keyValues) {
        Objects.requireNonNull(keyValues, "keyValues must not be null");
        if (keyValues.isEmpty()) return;
        redisTemplate.opsForValue().multiSet(keyValues);
        // log.debug("[RedisManager] MSET size={}", keyValues.size());
    }

    public boolean mSetIfAbsent(final Map<String, String> keyValues) {
        Objects.requireNonNull(keyValues, "keyValues must not be null");
        if (keyValues.isEmpty()) return true;
        final Boolean ok = redisTemplate.opsForValue().multiSetIfAbsent(keyValues);
        return Boolean.TRUE.equals(ok);
    }
}