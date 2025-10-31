package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminRedisTicketTypeSaver {
    private final RedisManager redisManager;

    public void saveTicketTypesInRedis(final List<TicketType> ticketTypes) {
        final Map<String, String> ticketTypeKeyValue = new HashMap<>();
        ticketTypes.forEach(ticketType -> {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + ticketType.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            ticketTypeKeyValue.put(key, String.valueOf(ticketType.getTotalTicketCount()));
        });
        redisManager.mSet(ticketTypeKeyValue);
        log.info("[Redis Init] ticketType Key Value = {}", ticketTypeKeyValue);
    }
}
