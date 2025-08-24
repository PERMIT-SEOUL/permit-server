package com.permitseoul.permitserver.domain.admin.guestticket.core.facade;

import com.permitseoul.permitserver.domain.admin.guestticket.core.component.AdminGuestTicketRetriever;
import com.permitseoul.permitserver.domain.admin.guestticket.core.component.AdminGuestTicketSaver;
import com.permitseoul.permitserver.domain.admin.guestticket.core.component.AdminGuestTicketUpdater;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.global.TicketOrCouponCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestTicketFacade {
    private final AdminGuestTicketSaver adminGuestTicketSaver;
    private final AdminGuestTicketUpdater adminGuestTicketUpdater;
    private final AdminGuestTicketRetriever adminGuestTicketRetriever;

    @Transactional
    public List<GuestTicketEntity> saveGuestTickets(final long eventId, final long guestId, final int count) {
        return adminGuestTicketSaver.saveGuestTickets(generateGuestTickets(eventId, guestId, count));
    }

    @Transactional
    public void updateGuestTicketUsable(final List<Long> guestTicketIds, final boolean usable) {
        final List<GuestTicketEntity> guestTicketEntities = adminGuestTicketRetriever.findAllGuestTicketsById(guestTicketIds);
        guestTicketEntities.forEach(guestTicketEntity -> adminGuestTicketUpdater.updateGuestTicketUsable(guestTicketEntity, usable));
    }

    private List<GuestTicketEntity> generateGuestTickets(final long eventId,
                                                         final long guestId,
                                                         final int count) {
        final List<GuestTicketEntity> generatedGuestTickets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final String guestTicketCode = TicketOrCouponCodeGenerator.generateCode();
            generatedGuestTickets.add(GuestTicketEntity.create(eventId, guestId, guestTicketCode));
        }
        return generatedGuestTickets;
    }
}
