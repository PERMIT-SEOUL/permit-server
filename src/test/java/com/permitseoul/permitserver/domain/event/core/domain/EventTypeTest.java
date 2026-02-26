package com.permitseoul.permitserver.domain.event.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EventType 테스트")
class EventTypeTest {

    @Nested
    @DisplayName("열거값 기본 검증")
    class EnumBasics {

        @Test
        @DisplayName("열거값은 3개이다")
        void hasThreeValues() {
            assertThat(EventType.values()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("displayName 필드 검증")
    class DisplayNameField {

        @ParameterizedTest(name = "{0} → \"{1}\"")
        @CsvSource({
                "PERMIT, PERMIT",
                "CEILING, ceiling service",
                "OLYMPAN, Olympan"
        })
        @DisplayName("각 이벤트 타입의 displayName이 올바르다")
        void hasCorrectDisplayName(final String enumName, final String expectedDisplayName) {
            // given
            final EventType eventType = EventType.valueOf(enumName);

            // when
            final String displayName = eventType.getDisplayName();

            // then
            assertThat(displayName).isEqualTo(expectedDisplayName);
        }
    }
}
