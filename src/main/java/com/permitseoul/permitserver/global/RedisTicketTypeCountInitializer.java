package com.permitseoul.permitserver.global;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTicketTypeCountInitializer implements ApplicationRunner {
    private final TicketTypeRepository ticketTypeRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<TicketTypeEntity> ticketTypes = ticketTypeRepository.findAll();

        ticketTypes.forEach(ticketType -> {
            String key = Constants.REDIS_TICKET_TYPE_KEY_NAME  + ticketType.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            redisTemplate.opsForValue().set(key, String.valueOf(ticketType.getRemainTicketCount())); //todo: 개발에서는 set, 운영에서는 setIfAbsent로 사용
            log.info("서버 시작 시, redis 티켓 재고 초기화",
                    keyValue("ticket_type_id", ticketType.getTicketTypeId()),
                    keyValue("remain_count", ticketType.getRemainTicketCount())
            );
        });
    }
}
