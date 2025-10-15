package com.permitseoul.permitserver.domain.eventtimetable.stage.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableStageRepository extends JpaRepository<TimetableStageEntity, Long> {
    List<TimetableStageEntity> findAllByTimetableId(final long timetableId);

    Optional<TimetableStageEntity> findByNotionStageRowId(final String notionStageRowId);
}
