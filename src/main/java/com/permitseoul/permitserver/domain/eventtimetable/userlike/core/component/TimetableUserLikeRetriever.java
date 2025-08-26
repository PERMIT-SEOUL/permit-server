package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.exception.TimetableUserLikeNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeRetriever {
    private final TimetableUserLikeRepository timetableUserLikeRepository;

    @Transactional(readOnly = true)
    public List<Long> findLikedBlockIdsIn(long userId, final List<Long> blockIds) {
        return timetableUserLikeRepository.findLikedBlockIdsIn(userId, blockIds);
    }

    @Transactional(readOnly = true)
    public boolean isExistUserLikeByUserIdAndBlockId(final long  userId, final long blockId) {
        return timetableUserLikeRepository.existsByUserIdAndTimetableBlockId(userId, blockId);
    }

    @Transactional(readOnly = true)
    public TimetableUserLikeEntity findByUserIdAndBlockId(final long userId, final long blockId) {
        return timetableUserLikeRepository.findByUserIdAndTimetableBlockId(userId, blockId).orElseThrow(TimetableUserLikeNotfoundException::new);
    }


}
