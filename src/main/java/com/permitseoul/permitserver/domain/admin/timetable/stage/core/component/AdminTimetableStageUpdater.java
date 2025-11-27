package com.permitseoul.permitserver.domain.admin.timetable.stage.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminTimetableStageUpdater {

    public void updateTimetableStageName(final TimetableStageEntity timetableStageEntity, final String newStageName) {
        timetableStageEntity.updateStageName(newStageName);
    }

    public void updateTimetableStageSequence(final TimetableStageEntity timetableStageEntity, final int newSequence) {
        timetableStageEntity.updateSequence(newSequence);
    }

}
