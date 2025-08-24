package com.permitseoul.permitserver.domain.admin.guestticket.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminGuestTicketUpdater {

    public void updateGuestTicketUsable(final GuestTicketEntity guestTicketEntity, final boolean usable) {
        guestTicketEntity.updateUsable(usable);
    }
}
