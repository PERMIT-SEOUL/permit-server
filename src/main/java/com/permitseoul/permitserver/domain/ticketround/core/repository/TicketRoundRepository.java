package com.permitseoul.permitserver.domain.ticketround.core.repository;

import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRoundRepository extends JpaRepository<TicketRoundEntity, Long> {
}
