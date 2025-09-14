package com.permitseoul.permitserver.domain.event.core.repository;

import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("""
        SELECT e FROM EventEntity e
        WHERE e.visibleStartAt <= :now
          AND e.visibleEndAt >= :now
    """)
    List<EventEntity> findVisibleEvents(@Param("now") final LocalDateTime now);
}
