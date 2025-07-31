package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimetableRepository extends JpaRepository<TimetableEntity, Long> {
    Optional<TimetableEntity> findByEventId(final long eventId);
}
