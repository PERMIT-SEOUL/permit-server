package com.permitseoul.permitserver.domain.admin.guestticket.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.repository.GuestTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestTicketSaver {
    private final GuestTicketRepository guestTicketRepository;

    public List<GuestTicketEntity> saveGuestTickets(final List<GuestTicketEntity> guestTicketEntities) {
        return guestTicketRepository.saveAll(guestTicketEntities);
    }
}
