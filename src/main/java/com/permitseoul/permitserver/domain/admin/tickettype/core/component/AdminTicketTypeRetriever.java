package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeRetriever {
    private final TicketTypeRepository ticketTypeRepository;

    public List<TicketType> getTicketTypesByTicketRounds(final List<Long> ticketRoundIds) {
        return ticketTypeRepository.findByTicketRoundIdIn(ticketRoundIds).stream()
                .map(TicketType::fromEntity)
                .toList();
    }
}
