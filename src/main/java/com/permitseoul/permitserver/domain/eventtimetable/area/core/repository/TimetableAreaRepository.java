package com.permitseoul.permitserver.domain.eventtimetable.area.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.entity.TimetableAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableAreaRepository extends JpaRepository<TimetableAreaEntity, Long> {
    List<TimetableAreaEntity> findAllByTimetableId(final long timetableId);
}
