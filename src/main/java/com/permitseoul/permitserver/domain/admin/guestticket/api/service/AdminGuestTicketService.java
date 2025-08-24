package com.permitseoul.permitserver.domain.admin.guestticket.api.service;

import com.permitseoul.permitserver.domain.admin.guestticket.core.facade.AdminGuestTicketFacade;
import com.permitseoul.permitserver.domain.admin.property.QrCodeProperties;
import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestRetriever;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import com.permitseoul.permitserver.domain.admin.guest.core.exception.AdminGuestNotFoundException;
import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import com.permitseoul.permitserver.domain.admin.guestticket.api.exception.AdminGuestTicketApiException;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.util.GuestTicketEmailSender;
import com.permitseoul.permitserver.domain.admin.util.QrCodeUtil;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminGuestTicketService {
    private final AdminGuestRetriever adminGuestRetriever;
    private final GuestTicketEmailSender guestTicketEmailSender;
    private final QrCodeProperties qrCodeProperties;
    private final EventRetriever eventRetriever;
    private final AdminGuestTicketFacade adminGuestTicketFacade;

    public void issueGuestTickets(final long eventId, final List<GuestTicketIssueRequest.GuestTicket> guestTicketList) {
        try {
            final Event event = eventRetriever.findEventById(eventId);
            for (GuestTicketIssueRequest.GuestTicket guestTicket : guestTicketList) {
                final long guestId = guestTicket.guestId();
                final int count = guestTicket.ticketCount();
                final Guest guest = adminGuestRetriever.findById(guestId);

                final List<GuestTicketEntity> savedTickets = adminGuestTicketFacade.saveGuestTickets(eventId, guestId, count);

                final List<String> guestTicketCodes = getGuestTicketCodes(savedTickets);
                final List<byte[]> qrPngs = getQrCodePngs(guestTicketCodes);
                sendGuestTicketMail(guest.getEmail(), guest.getName(), event.getName(), event.getEventType(), guestTicketCodes, qrPngs);

                updateGuestTicketUsable(savedTickets);
            }
        } catch (AdminGuestNotFoundException e) {
            throw new AdminGuestTicketApiException(ErrorCode.NOT_FOUND_GUEST);
        } catch (EventNotfoundException e) {
            throw new AdminGuestTicketApiException(ErrorCode.NOT_FOUND_EVENT);
        }
    }


    private List<String> getGuestTicketCodes(final List<GuestTicketEntity> guestTicketEntities) {
        return guestTicketEntities.stream()
                .map(GuestTicketEntity::getGuestTicketCode)
                .toList();
    }

    private List<byte[]> getQrCodePngs(final List<String> guestTicketCodes) {
        return guestTicketCodes.stream()
                .map(code -> QrCodeUtil.generatePng(qrCodeProperties.link(), code))
                .toList();
    }

    private void sendGuestTicketMail(final String guestEmail,
                                     final String guestName,
                                     final String eventName,
                                     final EventType eventType,
                                     final List<String> guestTicketCodes,
                                     final List<byte[]> qrPngs) {
        guestTicketEmailSender.sendGuestTicketsEmail(
                guestEmail,
                guestName,
                eventName,
                eventType,
                guestTicketCodes,
                qrPngs
        );
    }

    private void updateGuestTicketUsable(final List<GuestTicketEntity> guestTicketEntities) {
        final List<Long> ids = guestTicketEntities.stream()
                .map(GuestTicketEntity::getGuestTicketId) // guestTicketId getter 필요 (@Getter)
                .toList();
        adminGuestTicketFacade.updateGuestTicketUsable(ids, true);
    }
}
