package com.permitseoul.permitserver.domain.ticket.api.dto;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;

import java.util.List;

public record UserBuyTicketInfo(
        List<Order> orders
) {
    public record Order(
            String orderDate,
            String orderId,
            String eventName,
            boolean canCancel,
            List<TicketInfo> ticketInfo
    ) { }

    public enum TicketStatusForUi {
        USABLE, USED, CANCELED, EXPIRED
    }

    public record TicketInfo(
            String ticketCode,
            String ticketName,
            TicketStatusForUi ticketStatus,
            String ticketDate,
            String ticketTime
    ) { }
}
