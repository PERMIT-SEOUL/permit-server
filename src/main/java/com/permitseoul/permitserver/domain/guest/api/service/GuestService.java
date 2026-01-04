package com.permitseoul.permitserver.domain.guest.api.service;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.exception.GuestTicketNotFoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.guest.api.dto.res.GuestTicketValidateResponse;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestNotFoundException;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestTicketIllegalException;
import com.permitseoul.permitserver.domain.guest.core.component.GuestRetriever;
import com.permitseoul.permitserver.domain.guest.core.component.GuestUpdater;
import com.permitseoul.permitserver.domain.guest.core.domain.GuestTicket;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRetriever guestRetriever;
    private final EventRetriever eventRetriever;
    private final GuestUpdater guestUpdater;

    public GuestTicketValidateResponse validateGuestTicket(final String ticketCode) {
        try {
            final GuestTicket guestTicket = guestRetriever.findGuestTicketByTicketCode(ticketCode);
            validateGuestTicketStatus(guestTicket.getStatus());

            final Event event = eventRetriever.findEventById(guestTicket.getEventId());
            return GuestTicketValidateResponse.of(event.getName());
        } catch (GuestTicketNotFoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_GUEST_TICKET);
        } catch (EventNotfoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    @Transactional
    public void confirmGuestTicketByStaffCheckCode(final String ticketCode, final String checkCodeFromStaff) {
        try {
            final GuestTicketEntity guestTicketEntity = guestRetriever.findGuestTicketEntityByTicketCode(ticketCode);
            validateGuestTicketStatus(guestTicketEntity.getStatus());

            final Event event = eventRetriever.findEventById(guestTicketEntity.getEventId());
            validateGuestTicketByCheckCode(event.getTicketCheckCode(), checkCodeFromStaff);

            guestUpdater.updateGuestTicketStatus(guestTicketEntity, GuestTicketStatus.USED);
        } catch (GuestTicketNotFoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_GUEST_TICKET);
        } catch (EventNotfoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    @Transactional
    public void confirmGuestTicketByStaffCamera(final String ticketCode) {
        try {
            final GuestTicketEntity guestTicketEntity = guestRetriever.findGuestTicketEntityByTicketCode(ticketCode);
            validateGuestTicketStatus(guestTicketEntity.getStatus());

            guestUpdater.updateGuestTicketStatus(guestTicketEntity, GuestTicketStatus.USED);
        } catch (GuestTicketNotFoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_GUEST_TICKET);
        } catch (EventNotfoundException e) {
            throw new GuestNotFoundException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    private void validateGuestTicketStatus(final GuestTicketStatus status) {
        if(status == GuestTicketStatus.USED) {
            throw new GuestTicketIllegalException(ErrorCode.CONFLICT_ALREADY_USED_TICKET);
        }
    }

    private void validateGuestTicketByCheckCode(final String checkCode, final String checkCodeFromStaff) {
        if(!Objects.equals(checkCode, checkCodeFromStaff)) {
            throw new GuestTicketIllegalException(ErrorCode.BAD_REQUEST_TICKET_CHECK_CODE_ERROR);
        }
    }
}
