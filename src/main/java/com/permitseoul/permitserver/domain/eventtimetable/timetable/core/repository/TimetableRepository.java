package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntity, Long> {
    Optional<TimetableEntity> findByEventId(final long eventId);

    Optional<TimetableEntity> findByNotionTimetableDatasourceId(final String notionTimetableDatasourceId);

    Optional<TimetableEntity> findByNotionCategoryDatasourceId(final String notionCategoryDatasourceId);

    Optional<TimetableEntity> findByNotionStageDatasourceId(final String notionStageDatasourceId);


}
