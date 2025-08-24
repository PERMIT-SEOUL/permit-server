package com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record GuestTicketIssueRequest(
        @Positive(message = "eventId가 음수입니다.")
        long eventId,
        @NotEmpty(message = "guestTicketList는 비어있습니다.")
        List<@Valid GuestTicket> guestTicketList
) {
    public static GuestTicketIssueRequest of(final long eventId, final List<GuestTicket> guestTicketList) {
        return new GuestTicketIssueRequest(eventId, guestTicketList);
    }

    public record GuestTicket(
            @Positive(message = "Guest_id가 음수입니다.")
            long guestId,
            @Positive(message = "ticketCount가 음수입니다.")
            int ticketCount
    ) {
        public static GuestTicket of(final long guestId, final int ticketCount) {
            return new GuestTicket(guestId, ticketCount);
        }
    }
}
