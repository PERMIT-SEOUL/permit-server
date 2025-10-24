package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeSaver {
    private final TicketTypeRepository ticketTypeRepository;

    public List<TicketType> saveAllTicketTypes(final List<TicketTypeEntity> ticketTypeEntityList) {
        return ticketTypeRepository.saveAll(ticketTypeEntityList).stream()
                .map(TicketType::fromEntity)
                .toList();
    }

    public void saveTicketType(final long ticketRoundId,
                               final String name,
                               final BigDecimal price,
                               final int totalCount,
                               final LocalDateTime startAt,
                               final LocalDateTime endAt) {
        ticketTypeRepository.save(TicketTypeEntity.create(ticketRoundId, name, price, totalCount, startAt, endAt));
    }
}
