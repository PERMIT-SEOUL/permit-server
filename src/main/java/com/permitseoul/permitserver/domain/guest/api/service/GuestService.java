package com.permitseoul.permitserver.domain.guest.api.service;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import com.permitseoul.permitserver.domain.admin.guestticket.core.exception.GuestTicketNotFoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.guest.api.dto.res.GuestTicketValidateResponse;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestNotFoundException;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestTicketIllegalException;
import com.permitseoul.permitserver.domain.guest.core.component.GuestRetriever;
import com.permitseoul.permitserver.domain.guest.core.domain.GuestTicket;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRetriever guestRetriever;
    private final EventRetriever eventRetriever;

    public GuestTicketValidateResponse validateGuestTicket(final String ticketCode) {
        try {
            final GuestTicket guestTicket = guestRetriever.findGuestTicketByTicketCode(ticketCode);
            checkGuestTicketStatus(guestTicket.getStatus());

            final Event event = eventRetriever.findEventById(guestTicket.getEventId());
            return GuestTicketValidateResponse.of(event.getName());
        } catch (GuestTicketNotFoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_GUEST_TICKET);
        } catch (EventNotfoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    private void checkGuestTicketStatus(final GuestTicketStatus status) {
        if(status == GuestTicketStatus.USED) {
            throw new GuestTicketIllegalException(ErrorCode.CONFLICT_ALREADY_USED_TICKET);
        }
    }
}
