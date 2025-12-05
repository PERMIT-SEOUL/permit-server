package com.permitseoul.permitserver.domain.eventtimetable.blockmedia.repository;

import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableBlockMediaRepository extends JpaRepository<TimetableBlockMediaEntity, Long> {

    List<TimetableBlockMediaEntity> findAllByTimetableBlockIdOrderBySequenceAsc(final long timetableBlockId);

    void deleteAllByTimetableBlockId(final long timetableBlockId);
}
