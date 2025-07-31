package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeRetriever {
    private final TimetableUserLikeRepository timetableUserLikeRepository;

    @Transactional(readOnly = true)
    public List<Long> findLikedBlockIdsIn(long userId, final List<Long> blockIds) {
        return timetableUserLikeRepository.findLikedBlockIdsIn(userId, blockIds);
    }

    @Transactional(readOnly = true)
    public boolean isExistUserLikeByIdAndUserId(final long blockId, final long  userId) {
        return timetableUserLikeRepository.existsByTimetableBlockIdAndUserId(blockId, userId);
    }
}
