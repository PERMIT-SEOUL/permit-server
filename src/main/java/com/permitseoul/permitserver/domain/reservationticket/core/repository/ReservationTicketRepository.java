package com.permitseoul.permitserver.domain.reservationticket.core.repository;

import com.permitseoul.permitserver.domain.reservationticket.core.domain.entity.ReservationTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationTicketRepository extends JpaRepository<ReservationTicketEntity, Long> {
}
