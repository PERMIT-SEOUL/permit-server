package com.permitseoul.permitserver.domain.admin.guestticket.api.service;

import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestRetriever;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import com.permitseoul.permitserver.domain.admin.guest.core.exception.AdminGuestNotfoundException;
import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import com.permitseoul.permitserver.domain.admin.guestticket.api.exception.AdminGuestTicketApiException;
import com.permitseoul.permitserver.domain.admin.guestticket.core.component.AdminGuestTicketSaver;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminGuestTicketService {
    private final AdminGuestTicketSaver adminGuestTicketSaver;
    private final AdminGuestRetriever adminGuestRetriever;

    public void issueGuestTickets(final long eventId, final List<GuestTicketIssueRequest.GuestTicket> guestTicketList) {
        try {
            for (GuestTicketIssueRequest.GuestTicket guestTicket : guestTicketList) {
                final long guestId = guestTicket.id();
                final int count = guestTicket.ticketCount();
                final Guest guest = adminGuestRetriever.findById(guestId);

                final List<GuestTicketEntity> created = adminGuestTicketSaver.createTickets(eventId, guestId, count);


            }
        } catch (AdminGuestNotfoundException e) {
            throw new AdminGuestTicketApiException(ErrorCode.NOT_FOUND_GUEST);
        }
    }
}
