package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimetableUserLikeRepository extends JpaRepository<TimetableUserLikeEntity, Long> {

    @Query("SELECT l.timetableBlockId FROM TimetableUserLikeEntity l WHERE l.userId = :userId")
    List<Long> findAllBlockIdsLikedByUser(@Param("userId") final long userId);
}
