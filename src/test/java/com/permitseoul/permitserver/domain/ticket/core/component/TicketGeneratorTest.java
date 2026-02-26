package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.domain.ReservationStatus;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TicketGenerator 테스트")
class TicketGeneratorTest {

        private static final long USER_ID = 1L;
        private static final long EVENT_ID = 10L;
        private static final String ORDER_ID = "ORDER-20260119-001";
        private static final BigDecimal TICKET_TYPE_PRICE = new BigDecimal("60000");
        private static final BigDecimal COUPON_TOTAL_AMOUNT = new BigDecimal("50000");

        private Reservation createReservation(final String couponCode) {
                return new Reservation(
                                100L, "테스트 예약", USER_ID, EVENT_ID, ORDER_ID,
                                COUPON_TOTAL_AMOUNT, couponCode, ReservationStatus.RESERVED, null);
        }

        private ReservationTicket createReservationTicket(final long ticketTypeId, final int count) {
                return new ReservationTicket(1L, ticketTypeId, ORDER_ID, count);
        }

        private TicketTypeEntity createTicketTypeEntity(final long ticketTypeId) {
                final TicketTypeEntity entity = TicketTypeEntity.create(
                                1L, "VIP석", TICKET_TYPE_PRICE, 100,
                                LocalDateTime.of(2026, 1, 19, 17, 0),
                                LocalDateTime.of(2026, 1, 19, 21, 0));
                ReflectionTestUtils.setField(entity, "ticketTypeId", ticketTypeId);
                return entity;
        }

        @Nested
        @DisplayName("generatePublicTickets 메서드")
        class GeneratePublicTickets {

                @Test
                @DisplayName("쿠폰 없을 때 ticketTypeEntity의 가격을 사용한다")
                void usesTicketTypePriceWithoutCoupon() {
                        // given
                        final Reservation reservation = createReservation(null);
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(5L, 2));
                        final List<TicketTypeEntity> ticketTypes = List.of(createTicketTypeEntity(5L));

                        // when
                        final List<Ticket> tickets = TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes);

                        // then
                        assertThat(tickets).hasSize(2);
                        tickets.forEach(ticket -> assertThat(ticket.getTicketPrice())
                                        .isEqualByComparingTo(TICKET_TYPE_PRICE));
                }

                @Test
                @DisplayName("쿠폰 있을 때 reservation의 totalAmount를 사용한다")
                void usesTotalAmountWithCoupon() {
                        // given
                        final Reservation reservation = createReservation("COUPON-001");
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(5L, 1));
                        final List<TicketTypeEntity> ticketTypes = List.of(createTicketTypeEntity(5L));

                        // when
                        final List<Ticket> tickets = TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes);

                        // then
                        assertThat(tickets).hasSize(1);
                        assertThat(tickets.get(0).getTicketPrice()).isEqualByComparingTo(COUPON_TOTAL_AMOUNT);
                }

                @Test
                @DisplayName("생성된 Ticket의 기본 필드가 올바르게 설정된다")
                void setsTicketFieldsCorrectly() {
                        // given
                        final Reservation reservation = createReservation(null);
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(5L, 1));
                        final List<TicketTypeEntity> ticketTypes = List.of(createTicketTypeEntity(5L));

                        // when
                        final List<Ticket> tickets = TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes);

                        // then
                        final Ticket ticket = tickets.get(0);
                        assertThat(ticket.getUserId()).isEqualTo(USER_ID);
                        assertThat(ticket.getOrderId()).isEqualTo(ORDER_ID);
                        assertThat(ticket.getTicketTypeId()).isEqualTo(5L);
                        assertThat(ticket.getEventId()).isEqualTo(EVENT_ID);
                        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
                        assertThat(ticket.getTicketCode()).matches("[0-9A-F]{10}");
                }

                @Test
                @DisplayName("여러 ReservationTicket의 count 합만큼 Ticket이 생성된다")
                void generatesCorrectNumberOfTickets() {
                        // given
                        final Reservation reservation = createReservation(null);
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(5L, 3),
                                        createReservationTicket(6L, 2));
                        final List<TicketTypeEntity> ticketTypes = List.of(
                                        createTicketTypeEntity(5L),
                                        createTicketTypeEntity(6L));

                        // when
                        final List<Ticket> tickets = TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes);

                        // then
                        assertThat(tickets).hasSize(5);
                }

                @Test
                @DisplayName("ticketTypeId에 매칭되는 TicketTypeEntity가 없으면 IllegalArgumentException을 던진다")
                void throwsExceptionWhenTicketTypeNotFound() {
                        // given
                        final Reservation reservation = createReservation(null);
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(999L, 1));
                        final List<TicketTypeEntity> ticketTypes = List.of(createTicketTypeEntity(5L));

                        // when & then
                        assertThatThrownBy(() -> TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes))
                                        .isInstanceOf(IllegalArgumentException.class);
                }

                @Test
                @DisplayName("각 Ticket의 ticketCode가 서로 고유하다")
                void generatesUniqueTicketCodes() {
                        // given
                        final Reservation reservation = createReservation(null);
                        final List<ReservationTicket> reservationTickets = List.of(
                                        createReservationTicket(5L, 10));
                        final List<TicketTypeEntity> ticketTypes = List.of(createTicketTypeEntity(5L));

                        // when
                        final List<Ticket> tickets = TicketGenerator.generatePublicTickets(
                                        reservationTickets, USER_ID, reservation, ticketTypes);

                        // then
                        final long uniqueCodeCount = tickets.stream()
                                        .map(Ticket::getTicketCode)
                                        .distinct()
                                        .count();
                        assertThat(uniqueCodeCount).isEqualTo(10);
                }
        }
}
