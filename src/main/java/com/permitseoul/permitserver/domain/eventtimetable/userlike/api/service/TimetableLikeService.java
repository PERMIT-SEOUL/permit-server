package com.permitseoul.permitserver.domain.eventtimetable.userlike.api.service;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.ConflictTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRemover;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeSaver;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.exception.TimetableUserLikeNotfoundException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimetableLikeService {
    private final UserRetriever userRetriever;
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableUserLikeSaver timetableUserLikeSaver;
    private final TimetableUserLikeRetriever timetableUserLikeRetriever;
    private final TimetableUserLikeRemover timetableUserLikeRemover;

    public void likeBlock(final long userId, final long blockId) {
        try {
            validExistUserById(userId);
            validExistBlockById(blockId);
            if (timetableUserLikeRetriever.isExistUserLikeByUserIdAndBlockId(userId, blockId)) {
                throw new ConflictTimetableException(ErrorCode.CONFLICT_TIMETABLE_USER_LIKE);
            }
            timetableUserLikeSaver.saveTimetableBlockLike(userId, blockId);

        } catch (UserNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_USER);
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        }
    }

    public void disLikeBlock(final long userId, final long blockId) {
        try {
            final TimetableUserLikeEntity timetableUserLikeEntity = timetableUserLikeRetriever.findByUserIdAndBlockId(userId, blockId);
            timetableUserLikeRemover.dislikeUserLike(timetableUserLikeEntity);
        } catch (TimetableUserLikeNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_USER_LIKE);
        }
    }

    private void validExistUserById(final long userId) {
        userRetriever.validExistUserById(userId);
    }

    private void validExistBlockById(final long blockId) {
        timetableBlockRetriever.validExistTimetableBlock(blockId);
    }
}
