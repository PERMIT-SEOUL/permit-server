package com.permitseoul.permitserver.global;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TicketOrCouponCodeGenerator 테스트")
class TicketOrCouponCodeGeneratorTest {

    @Nested
    @DisplayName("generateCode 메서드")
    class GenerateCode {

        @Test
        @DisplayName("생성된 코드는 null이 아니다")
        void generatesNonNullCode() {
            // when
            final String code = TicketOrCouponCodeGenerator.generateCode();

            // then
            assertThat(code).isNotNull();
        }

        @Test
        @DisplayName("생성된 코드의 길이는 10자이다 (SHA-256의 앞 5바이트 = 10 hex chars)")
        void generatesCodeWithCorrectLength() {
            // when
            final String code = TicketOrCouponCodeGenerator.generateCode();

            // then
            assertThat(code).hasSize(10);
        }

        @Test
        @DisplayName("생성된 코드는 대문자 16진수 문자로만 구성된다")
        void generatesCodeWithUppercaseHexCharacters() {
            // when
            final String code = TicketOrCouponCodeGenerator.generateCode();

            // then
            assertThat(code).matches("[0-9A-F]{10}");
        }

        @RepeatedTest(10)
        @DisplayName("반복 생성 시에도 항상 올바른 형식을 유지한다")
        void maintainsFormatOnRepeatedGeneration() {
            // when
            final String code = TicketOrCouponCodeGenerator.generateCode();

            // then
            assertThat(code)
                    .isNotNull()
                    .hasSize(10)
                    .matches("[0-9A-F]{10}");
        }

        @Test
        @DisplayName("100개의 코드를 생성하면 모두 고유하다")
        void generatesUniqueCodesInBatch() {
            // given
            final Set<String> codes = new HashSet<>();
            final int batchSize = 100;

            // when
            for (int i = 0; i < batchSize; i++) {
                codes.add(TicketOrCouponCodeGenerator.generateCode());
            }

            // then
            assertThat(codes).hasSize(batchSize);
        }

        @Test
        @DisplayName("소문자를 포함하지 않는다")
        void doesNotContainLowercaseLetters() {
            // when
            final String code = TicketOrCouponCodeGenerator.generateCode();

            // then
            assertThat(code).isEqualTo(code.toUpperCase());
        }
    }
}
