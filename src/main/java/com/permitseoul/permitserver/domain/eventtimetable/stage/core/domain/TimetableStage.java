package com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TimetableStage {
    private final long timetableStageId;
    private final long timetableId;
    private final String stageName;
    private final int sequence;

    public static TimetableStage fromEntity(final TimetableStageEntity timetableStageEntity) {
        return new TimetableStage(
                timetableStageEntity.getTimetableStageId(),
                timetableStageEntity.getTimetableId(),
                timetableStageEntity.getStageName(),
                timetableStageEntity.getSequence()
        );
    }
}
