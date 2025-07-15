package com.permitseoul.permitserver.domain.ticket.core.repository;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findAllByOrderId(final String orderId);
}
