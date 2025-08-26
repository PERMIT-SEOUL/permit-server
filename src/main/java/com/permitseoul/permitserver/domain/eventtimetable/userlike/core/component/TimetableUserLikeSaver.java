package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeSaver {
    private final TimetableUserLikeRepository timetableUserLikeRepository;

    public void saveTimetableBlockLike(final long userId, final long timetableBlockId) {
        timetableUserLikeRepository.save(TimetableUserLikeEntity.create(userId, timetableBlockId));
    }
}
