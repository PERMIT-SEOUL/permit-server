package com.permitseoul.permitserver.domain.ticket.core.repository;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}
