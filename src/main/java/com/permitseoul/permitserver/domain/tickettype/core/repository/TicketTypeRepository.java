package com.permitseoul.permitserver.domain.tickettype.core.repository;

import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketTypeEntity t WHERE t.ticketTypeId = :ticketTypeId")
    Optional<TicketTypeEntity> findByIdForUpdate(@Param("ticketTypeId") long ticketTypeId);

    List<TicketTypeEntity> findAllByTicketRoundIdIn(final List<Long> ticketRoundIds);

    List<TicketTypeEntity> findAllByTicketRoundId(final Long ticketRoundId);

    void deleteAllByTicketRoundId(final long ticketRoundId);
}
