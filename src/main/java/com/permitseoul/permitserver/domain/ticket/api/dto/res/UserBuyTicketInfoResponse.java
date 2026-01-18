package com.permitseoul.permitserver.domain.ticket.api.dto.res;


import java.util.List;

public record UserBuyTicketInfoResponse(
        List<Order> orders
) {
    public record Order(
            String orderDate,
            String orderId,
            String eventName,
            String eventVenue,
            String paymentPrice,
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
