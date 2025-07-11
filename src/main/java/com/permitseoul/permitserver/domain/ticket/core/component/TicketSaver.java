package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TicketSaver {
    private final TicketRepository ticketRepository;

    public void saveTickets(final List<Ticket> tickets) {
        List<TicketEntity> ticketEntityList = tickets.stream()
                .map(ticket -> TicketEntity.builder()
                        .orderId(ticket.getOrderId())
                        .ticketCode(ticket.getTicketCode())
                        .userId(ticket.getUserId())
                        .eventId(ticket.getEventId())
                        .ticketTypeId(ticket.getTicketTypeId())
                        .build()
                ).toList();
        ticketRepository.saveAll(ticketEntityList);
    }
}
