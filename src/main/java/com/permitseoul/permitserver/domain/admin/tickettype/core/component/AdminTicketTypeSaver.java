package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeSaver {
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional
    public List<TicketType> saveAllTicketTypes(final List<TicketTypeEntity> ticketTypeEntityList) {
        return ticketTypeRepository.saveAll(ticketTypeEntityList).stream()
                .map(TicketType::fromEntity)
                .toList();
    }
}
