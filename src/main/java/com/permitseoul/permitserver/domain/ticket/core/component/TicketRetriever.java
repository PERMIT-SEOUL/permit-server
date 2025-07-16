package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketRetriever {
    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<TicketEntity> findAllTicketsByOrderIdAndUserId(final String orderId, final long userId) {
        final List<TicketEntity> ticketEntityList = ticketRepository.findAllByOrderIdAndUserId(orderId, userId);
        if(ticketEntityList.isEmpty()) {
            throw new TicketNotFoundException();
        }
        return ticketEntityList;
    }
}
