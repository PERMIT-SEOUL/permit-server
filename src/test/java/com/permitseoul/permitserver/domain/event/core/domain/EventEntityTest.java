package com.permitseoul.permitserver.domain.event.core.domain;

import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.exception.EventIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Event & EventEntity 테스트")
class EventEntityTest {

    private static final String NAME = "2026 신년 콘서트";
    private static final EventType EVENT_TYPE = EventType.PERMIT;
    private static final LocalDateTime START_AT = LocalDateTime.of(2026, 1, 19, 17, 0);
    private static final LocalDateTime END_AT = LocalDateTime.of(2026, 1, 19, 21, 0);
    private static final String VENUE = "서울 올림픽공원";
    private static final String LINE_UP = "아티스트A, 아티스트B";
    private static final String DETAILS = "2026년 신년 특별 공연";
    private static final int MIN_AGE = 15;
    private static final LocalDateTime VISIBLE_START_AT = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final LocalDateTime VISIBLE_END_AT = LocalDateTime.of(2026, 1, 19, 17, 0);
    private static final String TICKET_CHECK_CODE = "CHECK-2026";

    private EventEntity createTestEntity() {
        return EventEntity.create(NAME, EVENT_TYPE, START_AT, END_AT, VENUE,
                LINE_UP, DETAILS, MIN_AGE, VISIBLE_START_AT, VISIBLE_END_AT, TICKET_CHECK_CODE);
    }

    @Nested
    @DisplayName("EventEntity.create 메서드")
    class Create {

        @Test
        @DisplayName("정상적인 값으로 EventEntity를 생성한다")
        void createsEventEntitySuccessfully() {
            // when
            final EventEntity entity = createTestEntity();

            // then
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getEventType()).isEqualTo(EVENT_TYPE);
            assertThat(entity.getStartAt()).isEqualTo(START_AT);
            assertThat(entity.getEndAt()).isEqualTo(END_AT);
            assertThat(entity.getVenue()).isEqualTo(VENUE);
            assertThat(entity.getLineUp()).isEqualTo(LINE_UP);
            assertThat(entity.getDetails()).isEqualTo(DETAILS);
            assertThat(entity.getMinAge()).isEqualTo(MIN_AGE);
            assertThat(entity.getVisibleStartAt()).isEqualTo(VISIBLE_START_AT);
            assertThat(entity.getVisibleEndAt()).isEqualTo(VISIBLE_END_AT);
            assertThat(entity.getTicketCheckCode()).isEqualTo(TICKET_CHECK_CODE);
        }

        @Test
        @DisplayName("생성 직후 eventId는 null이다 (@GeneratedValue)")
        void eventIdIsNullAfterCreate() {
            // when
            final EventEntity entity = createTestEntity();

            // then
            assertThat(entity.getEventId()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEvent 메서드")
    class UpdateEvent {

        @Test
        @DisplayName("정상적인 값으로 이벤트를 업데이트한다")
        void updatesEventSuccessfully() {
            // given
            final EventEntity entity = createTestEntity();
            final String newName = "수정된 콘서트";
            final LocalDateTime newStart = LocalDateTime.of(2026, 2, 1, 18, 0);
            final LocalDateTime newEnd = LocalDateTime.of(2026, 2, 1, 22, 0);
            final LocalDateTime newVisibleStart = LocalDateTime.of(2026, 1, 15, 0, 0);
            final LocalDateTime newVisibleEnd = LocalDateTime.of(2026, 2, 1, 18, 0);

            // when
            entity.updateEvent(newName, EventType.CEILING, newStart, newEnd,
                    "새로운 장소", "새 라인업", "새 상세", 18,
                    newVisibleStart, newVisibleEnd, "NEW-CHECK");

            // then
            assertThat(entity.getName()).isEqualTo(newName);
            assertThat(entity.getEventType()).isEqualTo(EventType.CEILING);
            assertThat(entity.getStartAt()).isEqualTo(newStart);
            assertThat(entity.getEndAt()).isEqualTo(newEnd);
            assertThat(entity.getMinAge()).isEqualTo(18);
            assertThat(entity.getTicketCheckCode()).isEqualTo("NEW-CHECK");
        }

        @Test
        @DisplayName("startAt이 endAt보다 이후이면 EventIllegalArgumentException을 던진다")
        void throwsExceptionWhenStartAfterEnd() {
            // given
            final EventEntity entity = createTestEntity();
            final LocalDateTime invalidStart = LocalDateTime.of(2026, 2, 2, 0, 0);
            final LocalDateTime invalidEnd = LocalDateTime.of(2026, 2, 1, 0, 0);

            // when & then
            assertThatThrownBy(() -> entity.updateEvent(NAME, EVENT_TYPE,
                    invalidStart, invalidEnd, VENUE, LINE_UP, DETAILS, MIN_AGE,
                    VISIBLE_START_AT, VISIBLE_END_AT, TICKET_CHECK_CODE))
                    .isInstanceOf(EventIllegalArgumentException.class);
        }

        @Test
        @DisplayName("visibleStartAt이 visibleEndAt보다 이후이면 EventIllegalArgumentException을 던진다")
        void throwsExceptionWhenVisibleStartAfterVisibleEnd() {
            // given
            final EventEntity entity = createTestEntity();
            final LocalDateTime invalidVisibleStart = LocalDateTime.of(2026, 2, 2, 0, 0);
            final LocalDateTime invalidVisibleEnd = LocalDateTime.of(2026, 1, 1, 0, 0);

            // when & then
            assertThatThrownBy(() -> entity.updateEvent(NAME, EVENT_TYPE,
                    START_AT, END_AT, VENUE, LINE_UP, DETAILS, MIN_AGE,
                    invalidVisibleStart, invalidVisibleEnd, TICKET_CHECK_CODE))
                    .isInstanceOf(EventIllegalArgumentException.class);
        }

        @Test
        @DisplayName("startAt과 endAt이 같으면 정상 동작한다 (경계값)")
        void allowsSameStartAndEnd() {
            // given
            final EventEntity entity = createTestEntity();
            final LocalDateTime sameTime = LocalDateTime.of(2026, 2, 1, 18, 0);

            // when
            entity.updateEvent(NAME, EVENT_TYPE, sameTime, sameTime,
                    VENUE, LINE_UP, DETAILS, MIN_AGE,
                    VISIBLE_START_AT, VISIBLE_END_AT, TICKET_CHECK_CODE);

            // then
            assertThat(entity.getStartAt()).isEqualTo(sameTime);
            assertThat(entity.getEndAt()).isEqualTo(sameTime);
        }
    }

    @Nested
    @DisplayName("Event.fromEntity 메서드")
    class FromEntity {

        @Test
        @DisplayName("Entity의 모든 필드가 Domain 객체로 정확히 매핑된다")
        void mapsAllFieldsCorrectly() {
            // given
            final EventEntity entity = createTestEntity();
            ReflectionTestUtils.setField(entity, "eventId", 100L);

            // when
            final Event event = Event.fromEntity(entity);

            // then
            assertThat(event.getEventId()).isEqualTo(100L);
            assertThat(event.getName()).isEqualTo(NAME);
            assertThat(event.getEventType()).isEqualTo(EVENT_TYPE);
            assertThat(event.getStartAt()).isEqualTo(START_AT);
            assertThat(event.getEndAt()).isEqualTo(END_AT);
            assertThat(event.getVenue()).isEqualTo(VENUE);
            assertThat(event.getLineUp()).isEqualTo(LINE_UP);
            assertThat(event.getDetails()).isEqualTo(DETAILS);
            assertThat(event.getMinAge()).isEqualTo(MIN_AGE);
            assertThat(event.getVisibleStartAt()).isEqualTo(VISIBLE_START_AT);
            assertThat(event.getVisibleEndAt()).isEqualTo(VISIBLE_END_AT);
            assertThat(event.getTicketCheckCode()).isEqualTo(TICKET_CHECK_CODE);
        }
    }
}
