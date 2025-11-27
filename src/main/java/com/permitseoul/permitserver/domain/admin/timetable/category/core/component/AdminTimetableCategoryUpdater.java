package com.permitseoul.permitserver.domain.admin.timetable.category.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminTimetableCategoryUpdater {
    public void updateTimetableCategoryName(final TimetableCategoryEntity timetableCategoryEntity, final String newCategoryName) {
        timetableCategoryEntity.updateCategoryName(newCategoryName);
    }
}
