package com.permitseoul.permitserver.domain.eventtimetable.category.core.domain;

import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TimetableCategory {
    private final Long timetableCategoryId;
    private final long timetableId;
    private final String categoryName;
    private final String categoryBackgroundColor;
    private final String categoryLineColor;
    private final String categoryNotionId;

    public static TimetableCategory fromEntity(final TimetableCategoryEntity timetableCategoryEntity) {
        return new TimetableCategory(
                timetableCategoryEntity.getTimetableCategoryId(),
                timetableCategoryEntity.getTimetableId(),
                timetableCategoryEntity.getCategoryName(),
                timetableCategoryEntity.getCategoryBackgroundColor(),
                timetableCategoryEntity.getCategoryLineColor(),
                timetableCategoryEntity.getNotionCategoryRowId()
        );
    }

}
