package com.permitseoul.permitserver.domain.guest.core.domain;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GuestTicket {
    private final Long guestTicketId;
    private final long eventId;
    private final long guestId;
    private final String guestTicketCode;
    private final GuestTicketStatus status;
    private final LocalDateTime usedTime;

    public static GuestTicket fromEntity(final GuestTicketEntity guestTicketEntity) {
        return new GuestTicket(
                guestTicketEntity.getGuestTicketId(),
                guestTicketEntity.getEventId(),
                guestTicketEntity.getGuestId(),
                guestTicketEntity.getGuestTicketCode(),
                guestTicketEntity.getStatus(),
                guestTicketEntity.getUsedTime()
        );
    }
}