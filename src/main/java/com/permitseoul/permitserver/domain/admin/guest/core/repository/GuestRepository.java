package com.permitseoul.permitserver.domain.admin.guest.core.repository;

import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity, Long> {
}
