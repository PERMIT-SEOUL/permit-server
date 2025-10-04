package com.permitseoul.permitserver.domain.admin.ticket.api.dto.res;

import java.math.BigDecimal;
import java.util.List;

public record TicketRoundAndTicketTypeRes(
        long totalTicketCount,       // 전체 티켓 개수
        long totalTicketSoldCount,   // 전체 티켓 판매 개수
        long totalTicketSoldAmount, // 전체 티켓 판매된 총 가격
        List<TicketRoundWithTypes> ticketRoundsWithTypes
) {

    public static TicketRoundAndTicketTypeRes of(final long totalTicketCount,
                                                 final long totalTicketSoldCount,
                                                 final BigDecimal totalTicketSoldAmount,
                                                 final List<TicketRoundWithTypes> ticketRoundsWithTypes) {
        return new TicketRoundAndTicketTypeRes(
                totalTicketCount,
                totalTicketSoldCount,
                totalTicketSoldAmount.longValue(),
                ticketRoundsWithTypes
        );
    }

    public record TicketRoundWithTypes(
            long ticketRoundId,
            String ticketRoundName,
            String ticketRoundSalesStartDate, // yyyy.MM.dd
            String ticketRoundSalesStartTime, // HH:mm
            String ticketRoundSalesEndDate,   // yyyy.MM.dd
            String ticketRoundSalesEndTime,   // HH:mm
            List<TicketTypeInfo> ticketTypes
    ) {

        public static TicketRoundWithTypes of(final long ticketRoundId,
                                              final String ticketRoundName,
                                              final String ticketRoundSalesStartDate,
                                              final String ticketRoundSalesStartTime,
                                              final String ticketRoundSalesEndDate,
                                              final String ticketRoundSalesEndTime,
                                              final List<TicketTypeInfo> ticketTypes) {
            return new TicketRoundWithTypes(
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
                long ticketTypePrice,
                long ticketTypeSoldCount,
                long ticketTypeTotalCount,
                long ticketTypeSoldAmount,
                long ticketTypeRefundCount,
                long ticketTypeUsedCount

        ) {
            public static TicketTypeInfo of(final long ticketTypeId,
                                            final String ticketTypeName,
                                            final BigDecimal ticketTypePrice,
                                            final long ticketTypeSoldCount,
                                            final long ticketTypeTotalCount,
                                            final BigDecimal ticketTypeSoldAmount,
                                            final long ticketTypeRefundCount,
                                            final long ticketTypeUsedCount) {
                return new TicketTypeInfo(
                        ticketTypeId,
                        ticketTypeName,
                        ticketTypePrice.longValue(),
                        ticketTypeSoldCount,
                        ticketTypeTotalCount,
                        ticketTypeSoldAmount.longValue(),
                        ticketTypeRefundCount,
                        ticketTypeUsedCount
                );
            }
        }
    }
}
