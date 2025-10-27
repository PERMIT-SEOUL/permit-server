package com.permitseoul.permitserver.domain.admin.ticket.api.dto;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;

import java.util.List;

public record TicketTypeSplitResult(
        List<TicketTypeEntity> newTicketTypes,
        List<TicketTypeEntity> updatedTicketTypes
) {
}
