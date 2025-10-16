package com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.repository.TimetableBlockMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTimetableBlockMediaSaver {
    private final TimetableBlockMediaRepository timetableBlockMediaRepository;

    public void saveAllBlockMedia(final List<TimetableBlockMediaEntity> timetableBlockMediaEntities) {
        timetableBlockMediaRepository.saveAll(timetableBlockMediaEntities);
    }
}
