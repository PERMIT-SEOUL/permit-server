package com.permitseoul.permitserver.domain.ticket.core.repository;

import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    List<TicketEntity> findAllByOrderIdAndUserId(final String orderId, final long userId);

    List<TicketEntity> findAllByUserId(final long userId);

    Optional<TicketEntity> findByTicketCode(final String ticketCode);

    // 특정 타입, 특정 상태 개수
    long countByTicketTypeIdAndStatus(final long ticketTypeId, final TicketStatus status);

    // 특정 타입, 특정 상태들 개수
    long countByTicketTypeIdAndStatusIn(final long ticketTypeId, final Collection<TicketStatus> status);

    @Query("SELECT COALESCE(SUM(t.ticketPrice), 0.0) " +
            "FROM TicketEntity t " +
            "WHERE t.ticketTypeId = :ticketTypeId " +
            "AND t.status IN :statuses")
    BigDecimal sumTicketPriceByTicketTypeIdAndStatuses(
            @Param("ticketTypeId") final long ticketTypeId,
            @Param("statuses") final Iterable<TicketStatus> statuses
    );
}
