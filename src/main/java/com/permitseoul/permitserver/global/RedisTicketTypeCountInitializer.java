package com.permitseoul.permitserver.global;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisTicketTypeCountInitializer implements ApplicationRunner {
    private final TicketTypeRepository ticketTypeRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_TICKET_TYPE_KEY_PREFIX = "ticket_type:";
    private static final String REDIS_TICKET_TYPE_REMAIN_SUFFIX = ":remain";

    @Override
    public void run(ApplicationArguments args) {
        List<TicketTypeEntity> ticketTypes = ticketTypeRepository.findAll();

        ticketTypes.forEach(ticketType -> {
            String key = REDIS_TICKET_TYPE_KEY_PREFIX + ticketType.getTicketTypeId() + REDIS_TICKET_TYPE_REMAIN_SUFFIX;
            redisTemplate.opsForValue().set(key, String.valueOf(ticketType.getRemainTicketCount())); //todo: 개발에서는 set, 운영에서는 setIfAbsent로 사용
        });
    }
}
