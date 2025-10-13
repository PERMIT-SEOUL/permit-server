package com.permitseoul.permitserver.domain.eventtimetable.stage.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.TimetableStage;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.entity.TimetableStageEntity;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.exception.TimetableStageNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.repository.TimetableStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableStageRetriever {
    private final TimetableStageRepository timetableStageRepository;

    @Transactional(readOnly = true)
    public List<TimetableStage> findTimetableStageListByTimetableId(final long timeTableId) {
        final List<TimetableStageEntity> timetableStageEntityList = timetableStageRepository.findAllByTimetableId(timeTableId);
        if(timetableStageEntityList.isEmpty()) {
            throw new TimetableStageNotFoundException();
        }
        return timetableStageEntityList.stream()
                .map(TimetableStage::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TimetableStage findTimetableStageById(final long timetableStageId) {
        final TimetableStageEntity timetableStageEntity = timetableStageRepository.findById(timetableStageId).orElseThrow(TimetableStageNotFoundException::new);
        return TimetableStage.fromEntity(timetableStageEntity);
    }
}
