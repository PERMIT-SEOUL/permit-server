package com.permitseoul.permitserver.domain.admin.ticket.api.dto.res;

import java.math.BigDecimal;
import java.util.List;

public record TicketRoundAndTypeDetailRes(
        long ticketRoundId,
        String ticketRoundName,
        String ticketRoundSalesStartDate, // yyyy.MM.dd
        String ticketRoundSalesStartTime, // hh:mm
        String ticketRoundSalesEndDate,   // yyyy.MM.dd
        String ticketRoundSalesEndTime,   // hh:mm
        List<TicketTypeResponse> ticketTypes
) {
    public record TicketTypeResponse(
            long ticketTypeId,
            String ticketTypeName,
            BigDecimal ticketTypePrice,
            int ticketTypeCount,
            String ticketTypeStartDate, // yyyy.MM.dd
            String ticketTypeStartTime, // hh:mm
            String ticketTypeEndDate,   // yyyy.MM.dd
            String ticketTypeEndTime    // hh:mm
    ) {}
}