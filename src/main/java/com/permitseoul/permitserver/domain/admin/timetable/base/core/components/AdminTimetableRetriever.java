package com.permitseoul.permitserver.domain.admin.timetable.base.core.components;

import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminTimetableRetriever {
    private final TimetableRepository timetableRepository;

    @Transactional(readOnly = true)
    public Timetable findTimetableByEventId(final long eventId) {
        return Timetable.fromEntity(timetableRepository.findByEventId(eventId).orElseThrow(TimetableNotFoundException::new));
    }
}
