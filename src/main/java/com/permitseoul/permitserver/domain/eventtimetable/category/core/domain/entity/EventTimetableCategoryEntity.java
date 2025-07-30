package com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity;

import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventTimetableCategoryEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_timetable_category_id", nullable = false)
    private Long eventTimetableCategoryId;

    @Column(name = "event_timetable_id", nullable = false)
    private long eventTimetableId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_color", nullable = false)
    private String categoryColor;

    private EventTimetableCategoryEntity(long eventTimetableId, String categoryName, String categoryColor) {
        this.eventTimetableId = eventTimetableId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    public static EventTimetableCategoryEntity create(final long eventTimetableId,
                                                      final String categoryName,
                                                      final String categoryColor) {
        return new EventTimetableCategoryEntity(eventTimetableId, categoryName, categoryColor);
    }
}