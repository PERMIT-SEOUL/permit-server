package com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.repository.TimetableBlockMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTimetableBlockMediaRemover {
    private final TimetableBlockMediaRepository timetableBlockMediaRepository;

    public void deleteAllByTimetableBlockId(final long timetableBlockId) {
        timetableBlockMediaRepository.deleteAllByTimetableBlockId(timetableBlockId);
    }
}
