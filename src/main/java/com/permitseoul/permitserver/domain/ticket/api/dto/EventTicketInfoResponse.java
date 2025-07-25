package com.permitseoul.permitserver.domain.ticket.api.dto;

import java.util.List;

public record EventTicketInfoResponse(
        List<Round> rounds
) {
    public record Round(
            String roundId,
            String roundAvailable,
            String roundPrice,
            String roundName,
            List<TicketType> ticketTypes
    ) { }

    public record TicketType(
            String ticketTypeId,
            String ticketTypeName,
            String ticketTypeDate,
            String ticketTypeTime,
            String ticketTypePrice
    ) { }
}
