package com.permitseoul.permitserver.domain.ticket.core.component;

import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.global.TicketOrCouponCodeGenerator;

import java.util.List;
import java.util.stream.IntStream;

public abstract class TicketGenerator {

    public static List<Ticket> generatePublicTickets(final List<ReservationTicket> reservationTicketList,
                                              final long userId,
                                              final Reservation reservation,
                                              final List<TicketTypeEntity> ticketTypeEntities) {

        final boolean hasCoupon = reservation.getCouponCode() != null;

        return reservationTicketList.stream()
                .flatMap(reservationTicket -> {
                    // 현재 예약 티켓의 타입 ID로 대응되는 TicketTypeEntity 찾기
                    final TicketTypeEntity ticketTypeEntity = ticketTypeEntities.stream()
                            .filter(t -> t.getTicketTypeId() == reservationTicket.getTicketTypeId())
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("해당 ticketTypeId에 대한 TicketTypeEntity를 찾을 수 없습니다."));

                    // 실제 티켓 생성
                    return IntStream.range(0, reservationTicket.getCount())
                            .mapToObj(i -> Ticket.builder()
                                    .userId(userId)
                                    .orderId(reservationTicket.getOrderId())
                                    .ticketTypeId(reservationTicket.getTicketTypeId())
                                    .eventId(reservation.getEventId())
                                    .ticketCode(TicketOrCouponCodeGenerator.generateCode())
                                    .status(TicketStatus.RESERVED)
                                    .ticketPrice(
                                            hasCoupon
                                                    ? reservation.getTotalAmount()
                                                    : ticketTypeEntity.getTicketPrice()
                                    )
                                    .build());
                })
                .toList();
    }

}
