package com.permitseoul.permitserver.domain.ticket.api.dto.res;

import java.util.List;

public record EventTicketInfoResponse(
        List<Round> rounds
) {
    public record Round(
            long roundId,
            boolean roundAvailable,
            String roundPrice,
            String roundName,
            List<TicketType> ticketTypes
    ) { }

    public record TicketType(
            long ticketTypeId,
            String ticketTypeName,
            String ticketTypeDate,
            String ticketTypeTime,
            String ticketTypePrice,
            boolean isTicketSoldOut
    ) { }
}
