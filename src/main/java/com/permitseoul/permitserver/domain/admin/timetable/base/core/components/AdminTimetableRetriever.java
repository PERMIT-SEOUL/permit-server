package com.permitseoul.permitserver.domain.admin.timetable.base.core.components;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminTimetableRetriever {
    private final TimetableRepository timetableRepository;

    @Transactional(readOnly = true)
    public Timetable findTimetableByEventId(final long eventId) {
        return Timetable.fromEntity(timetableRepository.findByEventId(eventId).orElseThrow(TimetableNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public TimetableEntity findTimetableEntityById(final long timetableId) {
        return timetableRepository.findById(timetableId).orElseThrow(TimetableNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Timetable findTimetableByTimetableBlockDataSourceId(final String notionTimetableBlockDataSourceId) {
        return Timetable.fromEntity(timetableRepository.findByNotionTimetableDatasourceId(notionTimetableBlockDataSourceId).orElseThrow(TimetableNotFoundException::new)) ;
    }

    @Transactional(readOnly = true)
    public Timetable findTimetableByTimetableCategoryDataSourceId(final String notionTimetableCategoryDataSourceId) {
        return Timetable.fromEntity(timetableRepository.findByNotionCategoryDatasourceId(notionTimetableCategoryDataSourceId).orElseThrow(TimetableNotFoundException::new)) ;
    }

    @Transactional(readOnly = true)
    public Timetable findTimetableByTimetableStageDataSourceId(final String notionTimetableStageDataSourceId) {
        return Timetable.fromEntity(timetableRepository.findByNotionStageDatasourceId(notionTimetableStageDataSourceId).orElseThrow(TimetableNotFoundException::new)) ;
    }


}
