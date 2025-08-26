package com.permitseoul.permitserver.domain.eventtimetable.userlike.api.service;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeSaver;
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

    public void likeBlock(final long blockId, final long userId) {
        try {
            timetableBlockRetriever.validExistTimetableBlock(blockId);
            userRetriever.validExistUserById(userId);
            timetableUserLikeSaver.saveTimetableBlockLike(userId, blockId);

        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        } catch (UserNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_USER);
        }
    }
}
