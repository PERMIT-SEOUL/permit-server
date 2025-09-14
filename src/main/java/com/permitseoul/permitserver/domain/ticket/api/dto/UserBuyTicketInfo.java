package com.permitseoul.permitserver.domain.ticket.api.dto;


import java.util.List;

public record UserBuyTicketInfo(
        List<Order> orders
) {
    public record Order(
            String orderDate,
            String orderId,
            String eventName,
            String eventVenue,
            String refundedPrice,
            boolean canCancel,
            List<TicketInfo> ticketInfo
    ) { }

    public enum TicketStatusForUi {
        USABLE, USED, REFUNDED, EXPIRED
    }

    public record TicketInfo(
            String ticketCode,
            String ticketName,
            TicketStatusForUi ticketStatus,
            String ticketDate,
            String ticketTime
    ) { }
}
