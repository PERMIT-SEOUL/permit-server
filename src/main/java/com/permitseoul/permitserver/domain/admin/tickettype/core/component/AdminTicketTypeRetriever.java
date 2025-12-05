package com.permitseoul.permitserver.domain.admin.tickettype.core.component;

import com.permitseoul.permitserver.domain.admin.tickettype.core.exception.AdminTicketTypeNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTicketTypeRetriever {
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional(readOnly = true)
    public List<TicketType> getTicketTypesByTicketRounds(final List<Long> ticketRoundIds) {
        return ticketTypeRepository.findAllByTicketRoundIdIn(ticketRoundIds).stream()
                .map(TicketType::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketType> getTicketTypesByTicketRoundId(final Long ticketRoundId) {
        return ticketTypeRepository.findAllByTicketRoundId(ticketRoundId).stream()
                .map(TicketType::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketTypeEntity> getTicketTypeEntitiesByIds(final List<Long> ids) {
        final List<TicketTypeEntity> ticketTypeEntities = ticketTypeRepository.findAllById(ids);
        if (ticketTypeEntities.size() != ids.size()) {
            throw new AdminTicketTypeNotFoundException();
        }
        return ticketTypeEntities;
    }

    @Transactional(readOnly = true)
    public TicketType getTicketTypeById(final Long ticketTypeId) {
        return TicketType.fromEntity(ticketTypeRepository.findById(ticketTypeId).orElseThrow(AdminTicketTypeNotFoundException::new));
    }
}
