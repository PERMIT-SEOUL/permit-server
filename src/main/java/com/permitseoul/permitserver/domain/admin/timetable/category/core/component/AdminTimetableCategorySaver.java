package com.permitseoul.permitserver.domain.admin.timetable.category.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.repository.TimetableCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTimetableCategorySaver {
    private final TimetableCategoryRepository timetableCategoryRepository;

    public void saveAllTimetableCategoryEntities(final List<TimetableCategoryEntity> timetableCategoryEntities) {
        timetableCategoryRepository.saveAll(timetableCategoryEntities);
    }
}
