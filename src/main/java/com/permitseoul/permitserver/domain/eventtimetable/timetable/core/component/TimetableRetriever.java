package com.permitseoul.permitserver.domain.eventtimetable.timetable.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.entity.TimetableEntity;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TimetableRetriever {
    private final TimetableRepository timetableRepository;

    @Transactional(readOnly = true)
    public Timetable getTimetableByEventId(final long eventId) {
        final TimetableEntity timetableEntity = timetableRepository.findByEventId(eventId).orElseThrow(TimetableNotFoundException::new);
        return Timetable.fromEntity(timetableEntity);
    }
}
