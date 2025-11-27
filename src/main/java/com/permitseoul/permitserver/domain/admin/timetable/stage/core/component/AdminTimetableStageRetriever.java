package com.permitseoul.permitserver.domain.admin.timetable.stage.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.repository.TimetableStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTimetableStageRetriever {
    private final TimetableStageRepository timetableStageRepository;

    public TimetableStageEntity findTimetableStageByTimetableStageRowId(final String timetableStageRowId) {
        timetableStageRepository.findByNotionStageRowId(timetableStageRowId);
    }
}
