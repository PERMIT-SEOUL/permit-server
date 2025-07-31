package com.permitseoul.permitserver.domain.eventtimetable.category.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.repository.TimetableCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableCategoryRetriever {
    private final TimetableCategoryRepository timetableCategoryRepository;

    @Transactional(readOnly = true)
    public List<TimetableCategory> findAllTimetableCategory(final long timetableId) {
        final List<TimetableCategoryEntity> timetableCategoryEntityList = timetableCategoryRepository.findAllByTimetableId(timetableId);
        if (timetableCategoryEntityList.isEmpty()) {
            throw new TimetableCategoryNotfoundException();
        }
        return timetableCategoryEntityList.stream()
                .map(TimetableCategory::fromEntity)
                .toList();
    }

}
