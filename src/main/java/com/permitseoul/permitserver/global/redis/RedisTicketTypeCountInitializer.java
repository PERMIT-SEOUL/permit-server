package com.permitseoul.permitserver.global.redis;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import com.permitseoul.permitserver.global.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTicketTypeCountInitializer implements ApplicationRunner {
    private final TicketTypeRepository ticketTypeRepository;
    private final RedisManager redisManager;

    @Override
    public void run(ApplicationArguments args) {
        final List<TicketTypeEntity> ticketTypes = ticketTypeRepository.findAll();
        final List<String> createdKeys = new ArrayList<>(ticketTypes.size());

        try {
            ticketTypes.forEach(ticketType -> {
                final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + ticketType.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
                final boolean isCreated = redisManager.setIfAbsent(key, String.valueOf(ticketType.getRemainTicketCount()));

                if (isCreated) {
                    createdKeys.add(key);
                    log.info("[Redis] 서버 실행 - 초기 ticketType redis 생성, key = {}, value = {}", key, ticketType.getRemainTicketCount());
                } else {
                    log.info("[Redis] 서버 실행 - 기존 ticketType 유지(스킵), key = {}, value = {}", key, ticketType.getRemainTicketCount());
                }
            });
        } catch (Exception e) {
            for (String key : createdKeys) {
                try {
                    redisManager.delete(key);
                    log.warn("[Redis] 서버 실행 ticketType 롤백 DEL key={}", key);
                } catch (Exception ex) {
                    log.error("[Redis] 서버 실행 ticketType 롤백 실패 failed key={}", key, ex);
                }
            }
        }
    }
}
