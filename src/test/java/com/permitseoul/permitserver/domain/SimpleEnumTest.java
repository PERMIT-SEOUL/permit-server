package com.permitseoul.permitserver.domain;

import com.permitseoul.permitserver.domain.admin.base.core.domain.MediaType;
import com.permitseoul.permitserver.domain.payment.core.domain.Currency;
import com.permitseoul.permitserver.domain.payment.core.domain.PaymentType;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketUsability;
import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("단순 Enum 통합 테스트")
class SimpleEnumTest {

    @Nested
    @DisplayName("Currency")
    class CurrencyTest {

        @Test
        @DisplayName("열거값은 3개이다 (KRW, USD, JPY)")
        void hasThreeValues() {
            assertThat(Currency.values()).hasSize(3);
            assertThat(Currency.values()).containsExactly(Currency.KRW, Currency.USD, Currency.JPY);
        }

        @Test
        @DisplayName("valueOf로 KRW를 조회할 수 있다")
        void valueOfKRW() {
            assertThat(Currency.valueOf("KRW")).isEqualTo(Currency.KRW);
        }

        @Test
        @DisplayName("존재하지 않는 값은 IllegalArgumentException을 던진다")
        void throwsExceptionForInvalidValue() {
            assertThatThrownBy(() -> Currency.valueOf("EUR"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("PaymentType")
    class PaymentTypeTest {

        @Test
        @DisplayName("열거값은 8개이다")
        void hasEightValues() {
            assertThat(PaymentType.values()).hasSize(8);
        }

        @Test
        @DisplayName("CARD와 EASY_PAY가 포함되어 있다")
        void containsCardAndEasyPay() {
            assertThat(PaymentType.values())
                    .contains(PaymentType.CARD, PaymentType.EASY_PAY);
        }

        @Test
        @DisplayName("valueOf로 CARD를 조회할 수 있다")
        void valueOfCard() {
            assertThat(PaymentType.valueOf("CARD")).isEqualTo(PaymentType.CARD);
        }
    }

    @Nested
    @DisplayName("TicketStatus")
    class TicketStatusTest {

        @Test
        @DisplayName("열거값은 3개이다 (RESERVED, USED, CANCELED)")
        void hasThreeValues() {
            assertThat(TicketStatus.values()).hasSize(3);
            assertThat(TicketStatus.values()).containsExactly(
                    TicketStatus.RESERVED, TicketStatus.USED, TicketStatus.CANCELED);
        }
    }

    @Nested
    @DisplayName("TicketUsability")
    class TicketUsabilityTest {

        @Test
        @DisplayName("열거값은 2개이다 (USABLE, UNUSABLE)")
        void hasTwoValues() {
            assertThat(TicketUsability.values()).hasSize(2);
            assertThat(TicketUsability.values()).containsExactly(
                    TicketUsability.USABLE, TicketUsability.UNUSABLE);
        }
    }

    @Nested
    @DisplayName("UserRole")
    class UserRoleTest {

        @Test
        @DisplayName("열거값은 3개이다 (USER, ADMIN, STAFF)")
        void hasThreeValues() {
            assertThat(UserRole.values()).hasSize(3);
            assertThat(UserRole.values()).containsExactly(
                    UserRole.USER, UserRole.ADMIN, UserRole.STAFF);
        }
    }

    @Nested
    @DisplayName("Gender")
    class GenderTest {

        @Test
        @DisplayName("열거값은 2개이다 (MALE, FEMALE)")
        void hasTwoValues() {
            assertThat(Gender.values()).hasSize(2);
            assertThat(Gender.values()).containsExactly(Gender.MALE, Gender.FEMALE);
        }
    }

    @Nested
    @DisplayName("SocialType")
    class SocialTypeTest {

        @Test
        @DisplayName("열거값은 2개이다 (KAKAO, GOOGLE)")
        void hasTwoValues() {
            assertThat(SocialType.values()).hasSize(2);
            assertThat(SocialType.values()).containsExactly(SocialType.KAKAO, SocialType.GOOGLE);
        }
    }

    @Nested
    @DisplayName("MediaType")
    class MediaTypeTest {

        @Test
        @DisplayName("열거값은 2개이다 (IMAGE, VIDEO)")
        void hasTwoValues() {
            assertThat(MediaType.values()).hasSize(2);
            assertThat(MediaType.values()).containsExactly(MediaType.IMAGE, MediaType.VIDEO);
        }
    }
}
