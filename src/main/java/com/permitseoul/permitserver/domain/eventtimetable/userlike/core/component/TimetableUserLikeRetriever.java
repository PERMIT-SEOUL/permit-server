package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeRetriever {
    private final TimetableUserLikeRepository userLikeRepository;

    public List<Long> findLikedBlockIdsIn(long userId, final List<Long> blockIds) {
        return userLikeRepository.findLikedBlockIdsIn(userId, blockIds);
    }
}
