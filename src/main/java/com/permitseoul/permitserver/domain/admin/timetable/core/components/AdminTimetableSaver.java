package com.permitseoul.permitserver.domain.admin.timetable.core.components;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminTimetableSaver {
    private final TimetableRepository timetableRepository;

    public Timetable saveTimetable(final long eventId,
                                   final LocalDateTime timetableStartAt,
                                   final LocalDateTime timetableEndAt,
                                   final String notionTimetableDataSourceId,
                                   final String notionTimetableStageDataSourceId,
                                   final String notionTimetableCategoryDataSourceId) {
        return Timetable.fromEntity(timetableRepository.save(TimetableEntity.create(
                        eventId,
                        timetableStartAt,
                        timetableEndAt,
                        notionTimetableDataSourceId,
                        notionTimetableStageDataSourceId,
                        notionTimetableCategoryDataSourceId)
                )
        );
    }
}
