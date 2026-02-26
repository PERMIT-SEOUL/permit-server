package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.HashIdProperties;
import com.permitseoul.permitserver.global.exception.UrlSecureException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SecureUrlUtil 테스트")
class SecureUrlUtilTest {

    private SecureUrlUtil secureUrlUtil;

    @BeforeEach
    void setUp() {
        final HashIdProperties properties = new HashIdProperties("test-salt", 8);
        secureUrlUtil = new SecureUrlUtil(properties);
    }

    @Nested
    @DisplayName("encode 메서드")
    class Encode {

        @Test
        @DisplayName("유효한 ID를 인코딩하면 null이 아닌 문자열을 반환한다")
        void encodesValidId() {
            // when
            final String encoded = secureUrlUtil.encode(1L);

            // then
            assertThat(encoded).isNotNull().isNotEmpty();
        }

        @Test
        @DisplayName("인코딩된 값은 최소 지정 길이 이상이다")
        void encodedValueHasMinimumLength() {
            // when
            final String encoded = secureUrlUtil.encode(1L);

            // then
            assertThat(encoded.length()).isGreaterThanOrEqualTo(8);
        }

        @Test
        @DisplayName("같은 ID를 인코딩하면 항상 같은 결과를 반환한다")
        void encodingSameIdReturnsConsistentResult() {
            // when
            final String first = secureUrlUtil.encode(100L);
            final String second = secureUrlUtil.encode(100L);

            // then
            assertThat(first).isEqualTo(second);
        }

        @Test
        @DisplayName("다른 ID를 인코딩하면 다른 결과를 반환한다")
        void encodingDifferentIdsReturnsDifferentResults() {
            // when
            final String first = secureUrlUtil.encode(1L);
            final String second = secureUrlUtil.encode(2L);

            // then
            assertThat(first).isNotEqualTo(second);
        }

        @Test
        @DisplayName("null ID를 인코딩하면 UrlSecureException을 던지고 ErrorCode는 INTERNAL_ID_ENCODE_ERROR이다")
        void throwsExceptionWhenIdIsNull() {
            assertThatThrownBy(() -> secureUrlUtil.encode(null))
                    .isInstanceOf(UrlSecureException.class)
                    .satisfies(exception -> {
                        final UrlSecureException ex = (UrlSecureException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_ID_ENCODE_ERROR);
                    });
        }

        @Test
        @DisplayName("0을 인코딩해도 정상 동작한다")
        void encodesZeroSuccessfully() {
            // when
            final String encoded = secureUrlUtil.encode(0L);

            // then
            assertThat(encoded).isNotNull().isNotEmpty();
        }
    }

    @Nested
    @DisplayName("decode 메서드")
    class Decode {

        @Test
        @DisplayName("인코딩된 값을 디코딩하면 원래 ID를 반환한다")
        void decodesEncodedValueToOriginalId() {
            // given
            final long originalId = 42L;
            final String encoded = secureUrlUtil.encode(originalId);

            // when
            final long decoded = secureUrlUtil.decode(encoded);

            // then
            assertThat(decoded).isEqualTo(originalId);
        }

        @Test
        @DisplayName("encode → decode 라운드트립이 정상 동작한다")
        void roundTripWorksCorrectly() {
            // given
            final long[] testIds = { 0L, 1L, 100L, 999L, 123456L };

            for (final long id : testIds) {
                // when
                final String encoded = secureUrlUtil.encode(id);
                final long decoded = secureUrlUtil.decode(encoded);

                // then
                assertThat(decoded).as("ID %d의 encode-decode 라운드트립", id).isEqualTo(id);
            }
        }

        @Test
        @DisplayName("잘못된 해시를 디코딩하면 UrlSecureException을 던지고 ErrorCode는 BAD_REQUEST_ID_DECODE_ERROR이다")
        void throwsExceptionWhenHashIsInvalid() {
            assertThatThrownBy(() -> secureUrlUtil.decode("invalid-hash-!@#$%"))
                    .isInstanceOf(UrlSecureException.class)
                    .satisfies(exception -> {
                        final UrlSecureException ex = (UrlSecureException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST_ID_DECODE_ERROR);
                    });
        }

        @Test
        @DisplayName("빈 문자열을 디코딩하면 UrlSecureException을 던지고 ErrorCode는 BAD_REQUEST_ID_DECODE_ERROR이다")
        void throwsExceptionWhenHashIsEmpty() {
            assertThatThrownBy(() -> secureUrlUtil.decode(""))
                    .isInstanceOf(UrlSecureException.class)
                    .satisfies(exception -> {
                        final UrlSecureException ex = (UrlSecureException) exception;
                        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BAD_REQUEST_ID_DECODE_ERROR);
                    });
        }
    }
}
