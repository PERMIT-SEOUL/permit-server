package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "timetable_user_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_timetable_user_like_user_block",
                        columnNames = {"user_id", "timetable_block_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TimetableUserLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_user_like_id", nullable = false)
    private Long timetableUserLikeId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "timetable_block_id", nullable = false)
    private long timetableBlockId;

    private TimetableUserLikeEntity(long userId, long timetableBlockId) {
        this.userId = userId;
        this.timetableBlockId = timetableBlockId;
    }

    public static TimetableUserLikeEntity create(final long userId, final long timetableBlockId) {
        return new TimetableUserLikeEntity(userId, timetableBlockId);
    }
}
