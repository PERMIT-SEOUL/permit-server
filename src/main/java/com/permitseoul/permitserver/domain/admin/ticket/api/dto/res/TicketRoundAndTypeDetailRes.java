package com.permitseoul.permitserver.domain.admin.ticket.api.dto.res;

import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;

import java.math.BigDecimal;
import java.util.List;

public record TicketRoundAndTypeDetailRes(
        long ticketRoundId,
        String ticketRoundName,
        String ticketRoundSalesStartDate, // yyyy.MM.dd
        String ticketRoundSalesStartTime, // HH:mm
        String ticketRoundSalesEndDate,   // yyyy.MM.dd
        String ticketRoundSalesEndTime,   // HH:mm
        List<TicketTypeInfo> ticketTypes
) {

    public static TicketRoundAndTypeDetailRes of(final long ticketRoundId,
                                                 final String ticketRoundName,
                                                 final String ticketRoundSalesStartDate,
                                                 final String ticketRoundSalesStartTime,
                                                 final String ticketRoundSalesEndDate,
                                                 final String ticketRoundSalesEndTime,
                                                 final List<TicketTypeInfo> ticketTypes) {
        return new TicketRoundAndTypeDetailRes(
                ticketRoundId,
                ticketRoundName,
                ticketRoundSalesStartDate,
                ticketRoundSalesStartTime,
                ticketRoundSalesEndDate,
                ticketRoundSalesEndTime,
                ticketTypes
        );
    }

    public record TicketTypeInfo(
            long ticketTypeId,
            String ticketTypeName,
            BigDecimal ticketTypePrice,
            int ticketTypeCount,
            String ticketTypeStartDate, // yyyy.MM.dd
            String ticketTypeStartTime, // HH:mm
            String ticketTypeEndDate,   // yyyy.MM.dd
            String ticketTypeEndTime    // HH:mm
    ) {
        public static TicketTypeInfo of(final long ticketTypeId,
                                        final String ticketTypeName,
                                        final BigDecimal ticketTypePrice,
                                        final int ticketTypeCount,
                                        final String ticketTypeStartDate,
                                        final String ticketTypeStartTime,
                                        final String ticketTypeEndDate,
                                        final String ticketTypeEndTime) {

            return new TicketTypeInfo(
                    ticketTypeId,
                    ticketTypeName,
                    ticketTypePrice,
                    ticketTypeCount,
                    ticketTypeStartDate,
                    ticketTypeStartTime,
                    ticketTypeEndDate,
                    ticketTypeEndTime
            );
        }

    }
}