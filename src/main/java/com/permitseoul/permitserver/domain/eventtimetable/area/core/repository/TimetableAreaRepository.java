package com.permitseoul.permitserver.domain.eventtimetable.area.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.entity.TimetableAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableAreaRepository extends JpaRepository<TimetableAreaEntity, Long> {
    List<TimetableAreaEntity> findAllByTimetableId(final long timetableId);
}
