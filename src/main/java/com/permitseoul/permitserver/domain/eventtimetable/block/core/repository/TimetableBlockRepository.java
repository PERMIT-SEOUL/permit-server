package com.permitseoul.permitserver.domain.eventtimetable.block.core.repository;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableBlockRepository extends JpaRepository<TimetableBlockEntity, Long> {
    List<TimetableBlockEntity> findAllByTimetableId(final long timetableId);
}
