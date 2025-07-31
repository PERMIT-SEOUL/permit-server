package com.permitseoul.permitserver.domain.eventtimetable.category.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableCategoryRepository extends JpaRepository<TimetableCategoryEntity, Long> {
    List<TimetableCategoryEntity> findAllByTimetableId(final long timetableId);
}
