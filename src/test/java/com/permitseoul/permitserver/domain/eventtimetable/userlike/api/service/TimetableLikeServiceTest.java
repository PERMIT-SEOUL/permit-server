package com.permitseoul.permitserver.domain.eventtimetable.userlike.api.service;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRemover;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeSaver;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.domain.entity.TimetableUserLikeEntity;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.exception.TimetableUserLikeNotfoundException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableLikeService 테스트")
class TimetableLikeServiceTest {

    @Mock
    private UserRetriever userRetriever;
    @Mock
    private AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    @Mock
    private TimetableUserLikeSaver timetableUserLikeSaver;
    @Mock
    private TimetableUserLikeRetriever timetableUserLikeRetriever;
    @Mock
    private TimetableUserLikeRemover timetableUserLikeRemover;
    @InjectMocks
    private TimetableLikeService timetableLikeService;

    private static final long USER_ID = 1L;
    private static final long BLOCK_ID = 100L;

    @Nested
    @DisplayName("likeBlock")
    class LikeBlockTest {

        @Test
        @DisplayName("정상: 블록 좋아요")
        void success() {
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            doNothing().when(adminTimetableBlockRetriever).validExistTimetableBlock(BLOCK_ID);

            timetableLikeService.likeBlock(USER_ID, BLOCK_ID);

            verify(timetableUserLikeSaver).saveTimetableBlockLike(USER_ID, BLOCK_ID);
        }

        @Test
        @DisplayName("예외: 사용자 미존재 → NotfoundTimetableException")
        void throwsWhenUserNotFound() {
            doThrow(new UserNotFoundException()).when(userRetriever).validExistUserById(USER_ID);

            assertThatThrownBy(() -> timetableLikeService.likeBlock(USER_ID, BLOCK_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 블록 미존재 → NotfoundTimetableException")
        void throwsWhenBlockNotFound() {
            doNothing().when(userRetriever).validExistUserById(USER_ID);
            doThrow(new TimetableBlockNotfoundException()).when(adminTimetableBlockRetriever)
                    .validExistTimetableBlock(BLOCK_ID);

            assertThatThrownBy(() -> timetableLikeService.likeBlock(USER_ID, BLOCK_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }
    }

    @Nested
    @DisplayName("disLikeBlock")
    class DisLikeBlockTest {

        @Test
        @DisplayName("정상: 블록 좋아요 취소")
        void success() {
            final TimetableUserLikeEntity entity = mock(TimetableUserLikeEntity.class);
            when(timetableUserLikeRetriever.findByUserIdAndBlockId(USER_ID, BLOCK_ID)).thenReturn(entity);

            timetableLikeService.disLikeBlock(USER_ID, BLOCK_ID);

            verify(timetableUserLikeRemover).dislikeUserLike(entity);
        }

        @Test
        @DisplayName("예외: 좋아요 내역 미존재 → NotfoundTimetableException")
        void throwsWhenLikeNotFound() {
            when(timetableUserLikeRetriever.findByUserIdAndBlockId(USER_ID, BLOCK_ID))
                    .thenThrow(new TimetableUserLikeNotfoundException());

            assertThatThrownBy(() -> timetableLikeService.disLikeBlock(USER_ID, BLOCK_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }
    }
}
