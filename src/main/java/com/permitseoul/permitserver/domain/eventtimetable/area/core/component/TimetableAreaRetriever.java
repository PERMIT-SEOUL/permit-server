package com.permitseoul.permitserver.domain.eventtimetable.area.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.TimetableArea;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.entity.TimetableAreaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.exception.TimetableAreaNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.repository.TimetableAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableAreaRetriever {
    private final TimetableAreaRepository timetableAreaRepository;

    @Transactional(readOnly = true)
    public List<TimetableArea> findTimetableAreaListByTimetableId(final long timeTableId) {
        final List<TimetableAreaEntity> timetableEntityList = timetableAreaRepository.findAllByTimetableId(timeTableId);
        if(timetableEntityList.isEmpty()) {
            throw new TimetableAreaNotFoundException();
        }
        return timetableEntityList.stream()
                .map(TimetableArea::fromEntity)
                .toList();
    }
}
