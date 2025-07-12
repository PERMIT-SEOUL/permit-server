package com.permitseoul.permitserver.domain.tickettype.core.repository;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, Long> {
}
