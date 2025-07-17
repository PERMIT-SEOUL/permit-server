package com.permitseoul.permitserver.domain.reservationsession.core.repository;

import com.permitseoul.permitserver.domain.reservationsession.core.domain.entity.ReservationSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSessionRepository extends JpaRepository<ReservationSessionEntity, Long> {
}
