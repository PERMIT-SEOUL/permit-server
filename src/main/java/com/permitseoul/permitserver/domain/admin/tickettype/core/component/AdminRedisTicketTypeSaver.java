package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminRedisTicketTypeSaver {
    private final RedisManager redisManager;

    public void saveTicketTypesInRedis(final List<TicketType> ticketTypes) {
        ticketTypes.forEach(ticketType -> {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME
                    + ticketType.getTicketTypeId()
                    + Constants.REDIS_TICKET_TYPE_REMAIN;
            redisManager.set(key, String.valueOf(ticketType.getTotalTicketCount()), null);
            log.info("[Redis Init] ticketTypeId={} totalCount={}",
                    ticketType.getTicketTypeId(),
                    ticketType.getTotalTicketCount());
        });
    }
}
