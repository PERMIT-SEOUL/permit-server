package com.permitseoul.permitserver.domain.admin.guestticket.core.repository;

import com.permitseoul.permitserver.domain.admin.guestticket.api.dto.request.GuestTicketIssueRequest;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestTicketRepository extends JpaRepository<GuestTicketEntity, Long> {

    Optional<GuestTicketEntity> findByGuestTicketCode(final String guestTicketCode);
}
