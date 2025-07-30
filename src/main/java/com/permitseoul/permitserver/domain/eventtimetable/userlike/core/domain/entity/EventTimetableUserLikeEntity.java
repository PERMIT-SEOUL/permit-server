package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_timetable_user_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EventTimetableUserLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_timetable_user_like_id", nullable = false)
    private Long eventTimetableUserLikeId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "event_timetable_block_id", nullable = false)
    private long eventTimetableBlockId;

    private EventTimetableUserLikeEntity(long userId, long eventTimetableBlockId) {
        this.userId = userId;
        this.eventTimetableBlockId = eventTimetableBlockId;
    }

    public static EventTimetableUserLikeEntity create(final long userId, final long eventTimetableBlockId) {
        return new EventTimetableUserLikeEntity(userId, eventTimetableBlockId);
    }
}
