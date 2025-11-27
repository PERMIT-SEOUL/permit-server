package com.permitseoul.permitserver.domain.admin.timetable.category.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.repository.TimetableCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTimetableCategoryRetriever {
    private final TimetableCategoryRepository timetableCategoryRepository;

    public TimetableCategoryEntity findTimetableCategoryEntityByTimetableCategoryRowId(final String timetableCategoryRowId) {
        return timetableCategoryRepository.findByNotionCategoryRowId(timetableCategoryRowId).orElseThrow(TimetableCategoryNotfoundException::new);
    }
}
