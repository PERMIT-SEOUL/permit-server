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

        ticketTypes.forEach(ticketType -> {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME  + ticketType.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            final boolean isCreated = redisManager.setIfAbsent(key, String.valueOf(ticketType.getRemainTicketCount()));

            if (isCreated) {
                log.info("[Redis] 서버 실행 - 초기 ticketType redis 생성, key = {}, value = {}", key, ticketType.getRemainTicketCount());
            } else {
                log.info("[Redis] 서버 실행 - 기존 ticketType 유지(스킵), key = {}, value = {}", key, ticketType.getRemainTicketCount());
            }
        });
    }
}
