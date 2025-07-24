package com.permitseoul.permitserver.domain.eventImage.core.repository;

import com.permitseoul.permitserver.domain.eventImage.core.domain.entity.EventImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventImageRepository extends JpaRepository<EventImageEntity, Long> {

    @Query("SELECT e FROM EventImageEntity e WHERE e.eventId = :eventId AND e.sequence = 0")
    Optional<EventImageEntity> findThumbnailImageEntityByEventId(@Param("eventId") long eventId);
}
