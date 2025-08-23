package com.permitseoul.permitserver.domain.admin.guestticket.api.service;

import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminGuestTicketService {

    public void issueGuestTickets(final long eventId, final List<GuestTicketIssueRequest.GuestTicket> guestTicketList) {

    }
}
