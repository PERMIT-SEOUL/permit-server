package com.permitseoul.permitserver.domain.admin.guestticket.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.exception.GuestTicketNotFoundException;
import com.permitseoul.permitserver.domain.admin.guestticket.core.repository.GuestTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestTicketRetriever {
    private final GuestTicketRepository guestTicketRepository;

    public List<GuestTicketEntity> findAllGuestTicketsById(final List<Long> guestTicketIds) {
         final List<GuestTicketEntity> guestTicketEntities = guestTicketRepository.findAllById(guestTicketIds);
         if (ObjectUtils.isEmpty(guestTicketEntities)) {
             throw new GuestTicketNotFoundException();
         }
         return guestTicketEntities;
    }
}
