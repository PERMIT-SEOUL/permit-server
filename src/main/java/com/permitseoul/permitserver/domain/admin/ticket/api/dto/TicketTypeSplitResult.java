package com.permitseoul.permitserver.domain.admin.ticket.api.dto;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;

import java.util.List;
import java.util.Map;

public record TicketTypeSplitResult(
        List<TicketTypeEntity> newTicketTypes,
        List<TicketTypeEntity> updatedTicketTypes,
        Map<Long, Integer> updateDiffMap //(요청한 ticketType total - 기존에 있던 ticketTYpe total)
) {
    public static TicketTypeSplitResult of(
            List<TicketTypeEntity> newTypes,
            List<TicketTypeEntity> updatedTypes,
            Map<Long, Integer> diffMap
    ) {
        return new TicketTypeSplitResult(newTypes, updatedTypes, diffMap);
    }
}