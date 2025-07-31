package com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimetableCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_category_id", nullable = false)
    private Long timetableCategoryId;

    @Column(name = "timetable_id", nullable = false)
    private long timetableId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_color", nullable = false)
    private String categoryColor;

    private TimetableCategoryEntity(long timetableId, String categoryName, String categoryColor) {
        this.timetableId = timetableId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    public static TimetableCategoryEntity create(final long timetableId,
                                                 final String categoryName,
                                                 final String categoryColor) {
        return new TimetableCategoryEntity(timetableId, categoryName, categoryColor);
    }
}