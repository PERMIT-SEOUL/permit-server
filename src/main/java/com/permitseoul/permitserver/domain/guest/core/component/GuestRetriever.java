package com.permitseoul.permitserver.domain.guest.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.exception.GuestTicketNotFoundException;
import com.permitseoul.permitserver.domain.admin.guestticket.core.repository.GuestTicketRepository;
import com.permitseoul.permitserver.domain.guest.core.domain.GuestTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuestRetriever {
    private final GuestTicketRepository guestTicketRepository;

    public GuestTicket findGuestTicketByTicketCode(final String ticketCode) {
        return GuestTicket.fromEntity(guestTicketRepository.findByGuestTicketCode(ticketCode).orElseThrow(GuestTicketNotFoundException::new));
    }

    public GuestTicketEntity findGuestTicketEntityByTicketCode(final String ticketCode) {
        return guestTicketRepository.findByGuestTicketCode(ticketCode).orElseThrow(GuestTicketNotFoundException::new);
    }
}
