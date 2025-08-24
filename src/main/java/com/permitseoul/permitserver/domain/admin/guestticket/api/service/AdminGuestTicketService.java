package com.permitseoul.permitserver.domain.admin.guestticket.api.service;

import com.permitseoul.permitserver.domain.admin.property.QrCodeProperties;
import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestRetriever;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import com.permitseoul.permitserver.domain.admin.guest.core.exception.AdminGuestNotFoundException;
import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import com.permitseoul.permitserver.domain.admin.guestticket.api.exception.AdminGuestTicketApiException;
import com.permitseoul.permitserver.domain.admin.guestticket.core.component.AdminGuestTicketSaver;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.util.GuestTicketEmailSender;
import com.permitseoul.permitserver.domain.admin.util.QrCodeUtil;
import com.permitseoul.permitserver.domain.admin.util.exception.EmailSendException;
import com.permitseoul.permitserver.domain.admin.util.exception.QrCodeException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.global.TicketCodeGenerator;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminGuestTicketService {
    private final AdminGuestTicketSaver adminGuestTicketSaver;
    private final AdminGuestRetriever adminGuestRetriever;
    private final GuestTicketEmailSender guestTicketEmailSender;
    private final QrCodeProperties qrCodeProperties;
    private final EventRetriever eventRetriever;

    @Transactional
    public void issueGuestTickets(final long eventId, final List<GuestTicketIssueRequest.GuestTicket> guestTicketList) {
        try {
            final Event event = eventRetriever.findEventById(eventId);
            for (GuestTicketIssueRequest.GuestTicket guestTicket : guestTicketList) {
                final long guestId = guestTicket.id();
                final int count = guestTicket.ticketCount();
                final Guest guest = adminGuestRetriever.findById(guestId);

                final List<GuestTicketEntity> savedTickets = adminGuestTicketSaver.saveGuestTickets(generateGuestTickets(eventId, guestId, count));
                final List<String> guestTicketCodes = getGuestTicketCodes(savedTickets);
                final List<byte[]> qrPngs = getQrCodePngs(guestTicketCodes);

                sendGuestTicketMail(guest.getEmail(), guest.getName(), event.getName(), event.getEventType(), guestTicketCodes, qrPngs);
            }
        } catch (AdminGuestNotfoundException e) {
            throw new AdminGuestTicketApiException(ErrorCode.NOT_FOUND_GUEST);
        } catch (EventNotfoundException e) {
            throw new AdminGuestTicketApiException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    private List<GuestTicketEntity> generateGuestTickets(final long eventId, final long guestId, final int count) {
        final List<GuestTicketEntity> generatedGuestTickets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final String guestTicketCode = TicketCodeGenerator.generateTicketCode();
            generatedGuestTickets.add(GuestTicketEntity.create(eventId, guestId, guestTicketCode));
        }
        return generatedGuestTickets;
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
}
