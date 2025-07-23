package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.payment.api.service.TicketReservationPaymentFacade;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketRetriever {
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<Ticket> findAllTicketsByOrderIdAndUserId(final String orderId, final long userId) {
        final List<TicketEntity> ticketEntityList = ticketRepository.findAllByOrderIdAndUserId(orderId, userId);
        if(ObjectUtils.isEmpty(ticketEntityList)) {
            throw new TicketNotFoundException();
        }

        return ticketEntityList.stream()
                .map(Ticket::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketEntity> findAllTicketEntitiesById(final List<Long> ticketIds) {
        final List<TicketEntity> ticketEntityList = ticketRepository.findAllById(ticketIds);
        if(ObjectUtils.isEmpty(ticketEntityList)) {
            throw new TicketNotFoundException();
        }
        return ticketEntityList;
    }

}
