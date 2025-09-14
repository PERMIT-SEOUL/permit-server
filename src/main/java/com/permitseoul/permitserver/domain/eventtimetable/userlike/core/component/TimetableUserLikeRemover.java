package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeRemover {
    private final TimetableUserLikeRepository timetableUserLikeRepository;

    @Transactional
    public void dislikeUserLike(final TimetableUserLikeEntity timetableUserLikeEntity) {
        timetableUserLikeRepository.delete(timetableUserLikeEntity);
    }
}
