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

    @Column(name = "category_background_color", nullable = false)
    private String categoryBackgroundColor;

    @Column(name = "category_line_color", nullable = false)
    private String categoryLineColor;

    @Column(name = "notion_category_row_id", nullable = false)
    private String notionCategoryRowId;

    private TimetableCategoryEntity(long timetableId,
                                    String categoryName,
                                    String categoryBackgroundColor,
                                    String categoryLineColor,
                                    String notionCategoryRowId) {
        this.timetableId = timetableId;
        this.categoryName = categoryName;
        this.categoryBackgroundColor = categoryBackgroundColor;
        this.categoryLineColor = categoryLineColor;
        this.notionCategoryRowId = notionCategoryRowId;
    }

    public static TimetableCategoryEntity create(final long timetableId,
                                                 final String categoryName,
                                                 final String categoryBackgroundColor,
                                                 final String categoryLineColor,
                                                 final String notionCategoryRowId) {
        return new TimetableCategoryEntity(timetableId, categoryName, categoryBackgroundColor, categoryLineColor, notionCategoryRowId);
    }

    public void updateCategoryName(final String newCategoryName) {
        this.categoryName = newCategoryName;
    }

    public void updateBackgroundColor(final String newBackgroundColor) {
        this.categoryBackgroundColor = newBackgroundColor;
    }

    public void updateLineColor(final String newLineColor) {
        this.categoryLineColor = newLineColor;
    }
}
