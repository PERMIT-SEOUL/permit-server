package com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.repository.TimetableUserLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableUserLikeRetriever {
    private final TimetableUserLikeRepository userLikeRepository;

    public List<Long> findAllBlockIdsLikedByUserId(final long userId) {
        return userLikeRepository.findAllBlockIdsLikedByUser(userId);
    }
}
