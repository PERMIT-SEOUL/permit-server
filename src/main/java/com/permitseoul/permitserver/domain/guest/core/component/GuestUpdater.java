package com.permitseoul.permitserver.domain.guest.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import org.springframework.stereotype.Component;

@Component
public class GuestUpdater {

    public void updateGuestTicketStatus(final GuestTicketEntity guestTicketEntity, final GuestTicketStatus guestTicketStatus) {
        guestTicketEntity.updateStatus(guestTicketStatus);
    }
}
