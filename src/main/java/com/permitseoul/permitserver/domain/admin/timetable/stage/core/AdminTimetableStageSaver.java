package com.permitseoul.permitserver.domain.admin.timetable.stage.core;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.TimetableStage;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.repository.TimetableStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTimetableStageSaver {
    private final TimetableStageRepository timetableStageRepository;

    public void saveTimetableStage(final TimetableStage timetableStage) {}
}
