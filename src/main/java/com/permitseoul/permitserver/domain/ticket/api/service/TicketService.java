package com.permitseoul.permitserver.domain.ticket.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.ticket.api.dto.EventTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.dto.UserBuyTicketInfo;
import com.permitseoul.permitserver.domain.ticket.api.exception.NotFoundTicketException;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.global.exception.PriceFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
import com.permitseoul.permitserver.global.util.PriceFormatterUtil;
import com.permitseoul.permitserver.global.util.TimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRoundRetriever ticketRoundRetriever;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final TicketRetriever ticketRetriever;
    private final EventRetriever eventRetriever;

    private static final String ORDER_DATE_FORMAT = "yyyy-MM-dd";

    @Transactional(readOnly = true)
    public EventTicketInfoResponse getEventTicketInfo(final long eventId, final LocalDateTime now) {
        try {
            // 현재 구매가능한 티켓라운드와 이미 구매 기간 지난 라운드만 조회
            final List<TicketRound> ticketRoundList = ticketRoundRetriever.findSalesOrSalesEndTicketRoundByEventId(eventId, now);
            if (ticketRoundList.isEmpty()) {
                return new EventTicketInfoResponse(List.of());
            }

            final List<Long> ticketRoundIdList = extractTicketRoundIdList(ticketRoundList);

            // 각 티켓 라운드에 해당하는 티켓타입들을 가져와서 Map에 넣음
            final List<TicketType> ticketTypeList = ticketTypeRetriever.findTicketTypeListByRoundIdList(ticketRoundIdList);
            final Map<Long, List<TicketType>> ticketTypesByTicketRoundIdMap = ticketTypeList
                    .stream()
                    .collect(Collectors.groupingBy(TicketType::getTicketRoundId));

            // 라운드별로 티켓타입 최소 하나씩은 있는지 검증
            verifyEveryRoundHasTicketType(ticketTypesByTicketRoundIdMap, ticketRoundIdList);

            final List<EventTicketInfoResponse.Round> roundDtoList = ticketRoundList.stream()
                    .sorted(Comparator.comparing(TicketRound::getTicketRoundId))
                    .map(round -> createRoundDto(round, ticketTypesByTicketRoundIdMap, now)).toList();

            return new EventTicketInfoResponse(roundDtoList);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (PriceFormatException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE_PRICE);
        }
    }

    @Transactional(readOnly = true)
    public UserBuyTicketInfo getUserBuyTicketInfo(final long userId) {
        try {
            final List<Ticket> ticketList = ticketRetriever.findAllTicketsByUserId(userId);
            if (ticketList.isEmpty()) {
                return new UserBuyTicketInfo(List.of());
            }

            final Map<Long, TicketType> ticketTypeMap = getTicketTypeMap(ticketList);
            final Map<Long, Event> eventMap = getEventMap(ticketList);

            final Map<String, List<Ticket>> TicketListGroupedByOrderIdMap = ticketList.stream()
                    .collect(Collectors.groupingBy(Ticket::getOrderId));
            final List<UserBuyTicketInfo.Order> orders = convertToOrderList(TicketListGroupedByOrderIdMap, eventMap, ticketTypeMap);

            return new UserBuyTicketInfo(orders);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }

    private List<UserBuyTicketInfo.Order> convertToOrderList(final Map<String, List<Ticket>> ticketsGroupedByOrderId,
                                                             final Map<Long, Event> eventMap,
                                                             final Map<Long, TicketType> ticketTypeMap) {
        return ticketsGroupedByOrderId.entrySet().stream()
                .map(entry -> {
                    final String orderId = entry.getKey();
                    final List<Ticket> ticketsInOrder = entry.getValue();
                    final String orderDate = ticketsInOrder.get(0).getCreatedAt()
                            .format(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT));

                    final long eventId = ticketsInOrder.get(0).getEventId();
                    final String eventName = eventMap.get(eventId).getName();

                    final List<UserBuyTicketInfo.TicketInfo> ticketInfos = ticketsInOrder.stream()
                            .map(ticket -> {
                                final TicketType ticketType = ticketTypeMap.get(ticket.getTicketTypeId());
                                final boolean expired = isTicketDateExpired(ticketType.getTicketEndDate());

                                return new UserBuyTicketInfo.TicketInfo(
                                        ticket.getTicketCode(),
                                        ticketType.getTicketTypeName(),
                                        toUiStatus(ticket.getStatus(), expired),
                                        DateFormatterUtil.formatEventDate(ticketType.getTicketStartDate(), ticketType.getTicketEndDate()),
                                        TimeFormatterUtil.formatEventTime(ticketType.getTicketStartDate(), ticketType.getTicketEndDate())
                                );
                            }).toList();

                    // 한 오더내에서 모든 티켓이 USABLE 상태일 때만 취소 가능함
                    final boolean canCancel = ticketInfos.stream()
                            .allMatch(info -> info.ticketStatus() == UserBuyTicketInfo.TicketStatusForUi.USABLE);

                    return new UserBuyTicketInfo.Order(orderDate, orderId, eventName, canCancel, ticketInfos);
                })
                .sorted(Comparator.comparing(UserBuyTicketInfo.Order::orderDate).reversed())
                .toList();
    }

    private boolean isTicketDateExpired(final LocalDateTime endDate) {
        return LocalDateTime.now().isAfter(endDate);
    }

    private UserBuyTicketInfo.TicketStatusForUi toUiStatus(final TicketStatus status, final boolean expired) {
        if (expired && status == TicketStatus.RESERVED) {
            return UserBuyTicketInfo.TicketStatusForUi.EXPIRED;
        }

        return switch (status) {
            case RESERVED -> UserBuyTicketInfo.TicketStatusForUi.USABLE;
            case USED -> UserBuyTicketInfo.TicketStatusForUi.USED;
            case CANCELED -> UserBuyTicketInfo.TicketStatusForUi.CANCELED;
        };
    }

    private Map<Long, TicketType> getTicketTypeMap(final List<Ticket> tickets) {
        final List<Long> ticketTypeIds = tickets.stream()
                .map(Ticket::getTicketTypeId)
                .distinct()
                .toList();
        final Map<Long, TicketType> ticketTypeMap = ticketTypeRetriever.findAllTicketTypeById(ticketTypeIds).stream()
                .collect(Collectors.toMap(TicketType::getTicketTypeId, Function.identity()));
        if (!ticketTypeMap.keySet().containsAll(ticketTypeIds)) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }

        return ticketTypeMap;
    }

    private Map<Long, Event> getEventMap(final List<Ticket> tickets) {
        final List<Long> eventIds = tickets.stream()
                .map(Ticket::getEventId)
                .distinct()
                .toList();
        final Map<Long, Event> eventMap = eventRetriever.findAllEventsById(eventIds).stream()
                .collect(Collectors.toMap(Event::getEventId, Function.identity()));
        if (!eventMap.keySet().containsAll(eventIds)) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_EVENT);
        }

        return eventMap;
    }

    private EventTicketInfoResponse.Round createRoundDto(final TicketRound round,
                                                         final Map<Long, List<TicketType>> ticketTypesByTicketRoundIdMap,
                                                         final LocalDateTime now ) {
        final boolean isAvailable = isRoundAvailable(round, now);
        final List<TicketType> ticketTypeListInMap = Objects.requireNonNull(ticketTypesByTicketRoundIdMap.get(round.getTicketRoundId()));
        final String roundPrice = formatRoundPrice(ticketTypeListInMap);
        final List<EventTicketInfoResponse.TicketType> ticketTypeDtoList = getTicketTypeIfTicketRoundAvailableOrEmptyList(isAvailable, ticketTypeListInMap);

        return new EventTicketInfoResponse.Round(
                round.getTicketRoundId(),
                isAvailable,
                roundPrice,
                round.getTicketRoundTitle(),
                ticketTypeDtoList
        );
    }

    private boolean isRoundAvailable(final TicketRound round, final LocalDateTime now) {
        return !now.isBefore(round.getSalesStartDate()) && !now.isAfter(round.getSalesEndDate());
    }

    private void verifyEveryRoundHasTicketType(final Map<Long, List<TicketType>> ticketTypesByTicketRoundIdMap,
                                               final List<Long> ticketRoundIdList) {
        if (!ticketTypesByTicketRoundIdMap.keySet().containsAll(ticketRoundIdList)) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }

    private List<Long> extractTicketRoundIdList(final List<TicketRound> ticketRoundList) {
        return ticketRoundList.stream()
                .map(TicketRound::getTicketRoundId)
                .toList();
    }

    private List<EventTicketInfoResponse.TicketType> getTicketTypeIfTicketRoundAvailableOrEmptyList(final boolean isAvailable,
                                                                                                    final List<TicketType> ticketTypeListInMap) {
        return isAvailable
                ? ticketTypeListInMap.stream()
                .sorted(Comparator.comparing(TicketType::getTicketTypeId))
                .map(this::makeFormattedTicketTypeDto)
                .toList()
                : List.of();
    }

    private String formatRoundPrice(final List<TicketType> ticketTypes) {
        return PriceFormatterUtil.formatRoundPrice(
                ticketTypes.stream()
                        .map(TicketType::getTicketPrice)
                        .toList());
    }

    // 포맷팅된 티켓타입dto로 변환
    private EventTicketInfoResponse.TicketType makeFormattedTicketTypeDto(final TicketType ticketType) {
        final String formattedDate = DateFormatterUtil.formatEventDate(
                ticketType.getTicketStartDate(), ticketType.getTicketEndDate());
        final String formattedTime = ticketType.getTicketStartDate()
                .toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        return new EventTicketInfoResponse.TicketType(
                String.valueOf(ticketType.getTicketTypeId()),
                ticketType.getTicketTypeName(),
                formattedDate,
                formattedTime,
                PriceFormatterUtil.formatPrice(ticketType.getTicketPrice())
        );
    }
}
