package com.permitseoul.permitserver.domain.eventtimetable.block.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableBlockRepository extends JpaRepository<TimetableBlockEntity, Long> {
    List<TimetableBlockEntity> findAllByTimetableId(final long timetableId);
}
