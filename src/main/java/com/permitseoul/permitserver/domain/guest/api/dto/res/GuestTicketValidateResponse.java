package com.permitseoul.permitserver.domain.guest.api.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.DoorValidateUserTicket;

import java.time.LocalDateTime;

public record GuestTicketValidateResponse(
        String eventName,
        String ticketName
) {
    public static GuestTicketValidateResponse of(final String eventName) {
        return new GuestTicketValidateResponse(eventName, "Guest Ticket");
    }
}
