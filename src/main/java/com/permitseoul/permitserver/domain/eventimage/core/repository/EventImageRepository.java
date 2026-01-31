package com.permitseoul.permitserver.domain.eventimage.core.repository;

import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventImageRepository extends JpaRepository<EventImageEntity, Long> {

    @Query("SELECT e FROM EventImageEntity e WHERE e.eventId = :eventId AND e.sequence = 0")
    Optional<EventImageEntity> findThumbnailImageEntityByEventId(@Param("eventId") final long eventId);

    List<EventImageEntity> findAllByEventId(final long eventId);

    @Query("SELECT e FROM EventImageEntity e WHERE e.eventId IN :eventIds AND e.sequence = 0")
    List<EventImageEntity> findAllThumbnailsByEventIds(@Param("eventIds") final List<Long> eventIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from EventImageEntity e where e.eventId = :eventId")
    void deleteAllByEventId(@Param("eventId") final long eventId);
}
