package com.permitseoul.permitserver.domain.event.core.repository;

import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
