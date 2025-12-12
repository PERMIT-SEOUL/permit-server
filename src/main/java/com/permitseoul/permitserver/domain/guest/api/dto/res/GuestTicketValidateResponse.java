package com.permitseoul.permitserver.domain.guest.api.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.DoorValidateUserTicket;

import java.time.LocalDateTime;

public record GuestTicketValidateResponse(
        String eventName,
        String ticketName,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketStartDate,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketEndDate
) {
    public static GuestTicketValidateResponse of(final String eventName) {
        return new GuestTicketValidateResponse(eventName, "Guest Ticket", null, null);
    }
}
