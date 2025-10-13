package com.permitseoul.permitserver.domain.admin.timetable.api.service;

import com.permitseoul.permitserver.domain.admin.timetable.core.components.AdminTimetableSaver;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminTimetableService {
    private final AdminTimetableSaver adminTimetableSaver;

    public void saveInitialTimetableInfo(final long eventId,
                                         final LocalDateTime timetableStartAt,
                                         final LocalDateTime timetableEndAt,
                                         final String notionTimetableDataSourceId,
                                         final String notionTimetableStageDataSourceId,
                                         final String notionTimetableCategoryDataSourceId
                                         ) {

        //타임테이블 엔티티 생성
        final Timetable savedTimetable = adminTimetableSaver.saveTimetable(
                eventId,
                timetableStartAt,
                timetableEndAt,
                notionTimetableDataSourceId,
                notionTimetableCategoryDataSourceId,
                notionTimetableStageDataSourceId
        );



        //

    }
}
