package com.permitseoul.permitserver.domain.reservationticket.core.component;

import com.permitseoul.permitserver.domain.reservationticket.core.domain.ReservationTicket;
import com.permitseoul.permitserver.domain.reservationticket.core.domain.entity.ReservationTicketEntity;
import com.permitseoul.permitserver.domain.reservationticket.core.exception.ReservationTicketNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.repository.ReservationTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ReservationTicketRetriever {
    private final ReservationTicketRepository reservationTicketRepository;

    @Transactional(readOnly = true)
    public List<ReservationTicket> findAllByOrderId(final String orderId) {
        final List<ReservationTicketEntity> reservationTicketEntities = reservationTicketRepository.findAllByOrderId(orderId);
        if (ObjectUtils.isEmpty(reservationTicketEntities)) {
            throw new ReservationTicketNotFoundException();
        }
        return reservationTicketEntities.stream()
                .map(ReservationTicket::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTicket> findAllByOrderIds(List<String> orderIds) {
        final List<ReservationTicketEntity> reservationTicketEntityList = reservationTicketRepository.findAllByOrderIdIn(orderIds);
        if (reservationTicketEntityList == null || reservationTicketEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        return reservationTicketEntityList.stream()
                .map(ReservationTicket::fromEntity)
                .toList();
    }
}
