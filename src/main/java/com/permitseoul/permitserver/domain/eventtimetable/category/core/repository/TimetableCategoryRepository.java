package com.permitseoul.permitserver.domain.eventtimetable.category.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableCategoryRepository extends JpaRepository<TimetableCategoryEntity, Long> {
    List<TimetableCategoryEntity> findAllByTimetableId(final long timetableId);

    Optional<TimetableCategoryEntity> findByNotionCategoryRowId(final String notionCategoryRowId);
}
