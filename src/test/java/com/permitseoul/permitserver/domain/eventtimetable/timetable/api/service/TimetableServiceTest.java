package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.component.TimetableBlockMediaRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.TimetableBlockMedia;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.component.TimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.component.TimetableStageRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.TimetableStage;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.exception.TimetableStageNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableDetailResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.component.TimetableRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRetriever;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableService 테스트")
class TimetableServiceTest {

    @Mock
    private TimetableRetriever timetableRetriever;
    @Mock
    private TimetableStageRetriever timetableStageRetriever;
    @Mock
    private TimetableCategoryRetriever timetableCategoryRetriever;
    @Mock
    private AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    @Mock
    private TimetableBlockMediaRetriever timetableBlockMediaRetriever;
    @Mock
    private TimetableUserLikeRetriever timetableUserLikeRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private SecureUrlUtil secureUrlUtil;
    @InjectMocks
    private TimetableService timetableService;

    private static final long EVENT_ID = 100L;
    private static final long TIMETABLE_ID = 200L;
    private static final long BLOCK_ID = 300L;
    private static final long USER_ID = 1L;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 18, 14, 0);

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), "CHECK-CODE");
    }

    private Timetable createTimetable() {
        return new Timetable(TIMETABLE_ID, EVENT_ID, NOW.minusDays(1), NOW.plusDays(1), "notion-tt", "notion-stage",
                "notion-cat");
    }

    private TimetableStage createStage() {
        return new TimetableStage(1L, TIMETABLE_ID, "메인 스테이지", 1, "stage-notion-1");
    }

    private TimetableCategory createCategory() {
        return new TimetableCategory(1L, TIMETABLE_ID, "POP", "#FF0000", "#CC0000", "cat-notion-1");
    }

    private TimetableBlock createBlock() {
        return new TimetableBlock(BLOCK_ID, TIMETABLE_ID, "cat-notion-1", "stage-notion-1",
                NOW, NOW.plusHours(1), "공연 A", "아티스트", "공연 정보", "https://link.com", "block-notion-1");
    }

    @Nested
    @DisplayName("getEventTimetable")
    class GetEventTimetableTest {

        @Test
        @DisplayName("정상: userId 있음 → 좋아요 포함 타임테이블 조회")
        void successWithUserId() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenReturn(createTimetable());
            when(timetableStageRetriever.findTimetableStageListByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createStage()));
            when(timetableCategoryRetriever.findAllTimetableCategory(TIMETABLE_ID))
                    .thenReturn(List.of(createCategory()));
            when(adminTimetableBlockRetriever.findAllTimetableBlockByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createBlock()));
            when(timetableUserLikeRetriever.findLikedBlockIdsIn(eq(USER_ID), anyList())).thenReturn(List.of(BLOCK_ID));
            when(secureUrlUtil.encode(BLOCK_ID)).thenReturn("encoded-300");

            final TimetableResponse result = timetableService.getEventTimetable(EVENT_ID, USER_ID);

            assertThat(result.eventName()).isEqualTo("테스트 이벤트");
            assertThat(result.stages()).hasSize(1);
            assertThat(result.blocks()).hasSize(1);
            assertThat(result.blocks().get(0).isUserLiked()).isTrue();
        }

        @Test
        @DisplayName("정상: userId null → 좋아요 없이 조회")
        void successWithoutUserId() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenReturn(createTimetable());
            when(timetableStageRetriever.findTimetableStageListByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createStage()));
            when(timetableCategoryRetriever.findAllTimetableCategory(TIMETABLE_ID))
                    .thenReturn(List.of(createCategory()));
            when(adminTimetableBlockRetriever.findAllTimetableBlockByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createBlock()));
            when(secureUrlUtil.encode(BLOCK_ID)).thenReturn("encoded-300");

            final TimetableResponse result = timetableService.getEventTimetable(EVENT_ID, null);

            assertThat(result.blocks().get(0).isUserLiked()).isFalse();
        }

        @Test
        @DisplayName("예외: 이벤트 미존재 → NotfoundTimetableException")
        void throwsWhenEventNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenThrow(new EventNotfoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetable(EVENT_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 타임테이블 미존재 → NotfoundTimetableException")
        void throwsWhenTimetableNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenThrow(new TimetableNotFoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetable(EVENT_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 스테이지 미존재 → NotfoundTimetableException")
        void throwsWhenStageNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenReturn(createTimetable());
            when(timetableStageRetriever.findTimetableStageListByTimetableId(TIMETABLE_ID))
                    .thenThrow(new TimetableStageNotFoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetable(EVENT_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 카테고리 미존재 → NotfoundTimetableException")
        void throwsWhenCategoryNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenReturn(createTimetable());
            when(timetableStageRetriever.findTimetableStageListByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createStage()));
            when(timetableCategoryRetriever.findAllTimetableCategory(TIMETABLE_ID))
                    .thenThrow(new TimetableCategoryNotfoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetable(EVENT_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 블록 미존재 → NotfoundTimetableException")
        void throwsWhenBlockNotFound() {
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());
            when(timetableRetriever.getTimetableByEventId(EVENT_ID)).thenReturn(createTimetable());
            when(timetableStageRetriever.findTimetableStageListByTimetableId(TIMETABLE_ID))
                    .thenReturn(List.of(createStage()));
            when(timetableCategoryRetriever.findAllTimetableCategory(TIMETABLE_ID))
                    .thenReturn(List.of(createCategory()));
            when(adminTimetableBlockRetriever.findAllTimetableBlockByTimetableId(TIMETABLE_ID))
                    .thenThrow(new TimetableBlockNotfoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetable(EVENT_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }
    }

    @Nested
    @DisplayName("getEventTimetableDetail")
    class GetEventTimetableDetailTest {

        @Test
        @DisplayName("정상: userId 있음 → 좋아요 포함 상세 조회")
        void successWithUserId() {
            final TimetableBlock block = createBlock();
            final List<TimetableBlockMedia> mediaList = List.of(
                    new TimetableBlockMedia(1L, BLOCK_ID, 1, "https://media.com/img1.png"),
                    new TimetableBlockMedia(2L, BLOCK_ID, 2, "https://media.com/img2.png"));
            when(adminTimetableBlockRetriever.findTimetableBlockById(BLOCK_ID)).thenReturn(block);
            when(timetableBlockMediaRetriever.getAllTimetableBlockMediaByBlockId(BLOCK_ID)).thenReturn(mediaList);
            when(timetableCategoryRetriever.findTimetableCategoryByCategoryNotionRowId("cat-notion-1"))
                    .thenReturn(createCategory());
            when(timetableStageRetriever.findTimetableStageByStageNotionRowId("stage-notion-1"))
                    .thenReturn(createStage());
            when(timetableUserLikeRetriever.isExistUserLikeByUserIdAndBlockId(USER_ID, BLOCK_ID)).thenReturn(true);

            final TimetableDetailResponse result = timetableService.getEventTimetableDetail(BLOCK_ID, USER_ID);

            assertThat(result.blockName()).isEqualTo("공연 A");
            assertThat(result.blockCategory()).isEqualTo("POP");
            assertThat(result.stage()).isEqualTo("메인 스테이지");
            assertThat(result.isLiked()).isTrue();
            assertThat(result.media()).hasSize(2);
        }

        @Test
        @DisplayName("정상: userId null → 좋아요 false")
        void successWithoutUserId() {
            final TimetableBlock block = createBlock();
            when(adminTimetableBlockRetriever.findTimetableBlockById(BLOCK_ID)).thenReturn(block);
            when(timetableBlockMediaRetriever.getAllTimetableBlockMediaByBlockId(BLOCK_ID)).thenReturn(List.of());
            when(timetableCategoryRetriever.findTimetableCategoryByCategoryNotionRowId("cat-notion-1"))
                    .thenReturn(createCategory());
            when(timetableStageRetriever.findTimetableStageByStageNotionRowId("stage-notion-1"))
                    .thenReturn(createStage());

            final TimetableDetailResponse result = timetableService.getEventTimetableDetail(BLOCK_ID, null);

            assertThat(result.isLiked()).isFalse();
        }

        @Test
        @DisplayName("예외: 블록 미존재 → NotfoundTimetableException")
        void throwsWhenBlockNotFound() {
            when(adminTimetableBlockRetriever.findTimetableBlockById(BLOCK_ID))
                    .thenThrow(new TimetableBlockNotfoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetableDetail(BLOCK_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 카테고리 미존재 → NotfoundTimetableException")
        void throwsWhenCategoryNotFound() {
            final TimetableBlock block = createBlock();
            when(adminTimetableBlockRetriever.findTimetableBlockById(BLOCK_ID)).thenReturn(block);
            when(timetableBlockMediaRetriever.getAllTimetableBlockMediaByBlockId(BLOCK_ID)).thenReturn(List.of());
            when(timetableCategoryRetriever.findTimetableCategoryByCategoryNotionRowId("cat-notion-1"))
                    .thenThrow(new TimetableCategoryNotfoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetableDetail(BLOCK_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }

        @Test
        @DisplayName("예외: 스테이지 미존재 → NotfoundTimetableException")
        void throwsWhenStageNotFound() {
            final TimetableBlock block = createBlock();
            when(adminTimetableBlockRetriever.findTimetableBlockById(BLOCK_ID)).thenReturn(block);
            when(timetableBlockMediaRetriever.getAllTimetableBlockMediaByBlockId(BLOCK_ID)).thenReturn(List.of());
            when(timetableCategoryRetriever.findTimetableCategoryByCategoryNotionRowId("cat-notion-1"))
                    .thenReturn(createCategory());
            when(timetableStageRetriever.findTimetableStageByStageNotionRowId("stage-notion-1"))
                    .thenThrow(new TimetableStageNotFoundException());

            assertThatThrownBy(() -> timetableService.getEventTimetableDetail(BLOCK_ID, USER_ID))
                    .isInstanceOf(NotfoundTimetableException.class);
        }
    }
}
