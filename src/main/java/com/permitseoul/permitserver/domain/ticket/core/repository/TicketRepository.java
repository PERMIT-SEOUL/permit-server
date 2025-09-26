package com.permitseoul.permitserver.domain.ticket.core.repository;

import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findAllByOrderIdAndUserId(final String orderId, final long userId);

    List<TicketEntity> findAllByUserId(final long userId);

    Optional<TicketEntity> findByTicketCode(final String ticketCode);
}
