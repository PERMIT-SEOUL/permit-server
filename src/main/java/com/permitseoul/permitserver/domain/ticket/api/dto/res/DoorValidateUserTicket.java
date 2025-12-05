package com.permitseoul.permitserver.domain.ticket.api.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record DoorValidateUserTicket(
        String eventName,
        String ticketName,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketStartDate,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime ticketEndDate
) {
    public static DoorValidateUserTicket of(final String eventName,
                                            final String ticketName,
                                            final LocalDateTime ticketStartDate,
                                            final LocalDateTime ticketEndDate) {
        return new DoorValidateUserTicket(eventName, ticketName, ticketStartDate, ticketEndDate);
    }
}
