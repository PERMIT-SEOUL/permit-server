package com.permitseoul.permitserver.domain.admin.timetable.stage.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.repository.TimetableStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTimetableStageSaver {
    private final TimetableStageRepository timetableStageRepository;

    public void saveAllTimetableStagesEntities(final List<TimetableStageEntity> timetableStageEntities) {
        timetableStageRepository.saveAll(timetableStageEntities);
    }

    public void saveTimetableStage(final long timetableId, final String notionTimetableStageRowId) {
        timetableStageRepository.save(TimetableStageEntity.createEmptyRow(timetableId, notionTimetableStageRowId));
    }
}
