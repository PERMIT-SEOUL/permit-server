package com.permitseoul.permitserver.domain.eventimage.core.repository;

import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventImageRepository extends JpaRepository<EventImageEntity, Long> {

    @Query("SELECT e FROM EventImageEntity e WHERE e.eventId = :eventId AND e.sequence = 0")
    Optional<EventImageEntity> findThumbnailImageEntityByEventId(@Param("eventId") long eventId);

    List<EventImageEntity> findAllByEventId(final long eventId);
}
