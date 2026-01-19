package com.permitseoul.permitserver.domain.ticket.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.payment.api.exception.PaymentBadRequestException;
import com.permitseoul.permitserver.domain.payment.core.component.PaymentRetriever;
import com.permitseoul.permitserver.domain.payment.core.domain.Payment;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.DoorValidateUserTicket;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.EventTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.dto.res.UserBuyTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.exception.ConflictTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.DateTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.IllegalTicketException;
import com.permitseoul.permitserver.domain.ticket.api.exception.NotFoundTicketException;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketRetriever;
import com.permitseoul.permitserver.domain.ticket.core.component.TicketUpdater;
import com.permitseoul.permitserver.domain.ticket.core.domain.Ticket;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.domain.entity.TicketEntity;
import com.permitseoul.permitserver.domain.ticket.core.exception.TicketNotFoundException;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.exception.PriceFormatException;
import com.permitseoul.permitserver.global.exception.RedisKeyNotFoundException;
import com.permitseoul.permitserver.global.exception.RedisUnavailableException;
import com.permitseoul.permitserver.global.redis.RedisManager;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import com.permitseoul.permitserver.global.util.PriceFormatterUtil;
import com.permitseoul.permitserver.global.util.TimeFormatterUtil;
import org.springframework.dao.QueryTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRoundRetriever ticketRoundRetriever;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final TicketRetriever ticketRetriever;
    private final EventRetriever eventRetriever;
    private final PaymentRetriever paymentRetriever;
    private final RedisManager redisManager;
    private final TicketUpdater ticketUpdater;

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

            // ticketType soldOut 여부를 Redis에서 계산
            final Map<Long, Boolean> soldOutByTicketTypeId = buildSoldOutMapByTicketTypeId(ticketTypeList);
            final List<EventTicketInfoResponse.Round> roundDtoList = ticketRoundList.stream()
                    .sorted(Comparator.comparing(TicketRound::getTicketRoundId))
                    .map(round -> createRoundDto(round, ticketTypesByTicketRoundIdMap, soldOutByTicketTypeId, now))
                    .toList();

            return new EventTicketInfoResponse(roundDtoList);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (PriceFormatException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE_PRICE);
        }
    }

    @Transactional(readOnly = true)
    public UserBuyTicketInfoResponse getUserBuyTicketInfo(final Long userId) {
        if (userId == null) {
            return new UserBuyTicketInfoResponse(List.of());
        }
        try {
            final List<Ticket> ticketList = ticketRetriever.findAllTicketsByUserId(userId);
            if (ticketList.isEmpty()) {
                return new UserBuyTicketInfoResponse(List.of());
            }

            final Map<Long, TicketType> ticketTypeMap = getTicketTypeMap(ticketList);
            final Map<Long, Event> eventMap = getEventMap(ticketList);

            final Map<String, List<Ticket>> TicketListGroupedByOrderIdMap = ticketList.stream()
                    .collect(Collectors.groupingBy(Ticket::getOrderId));

            final Map<String, BigDecimal> paymentAmountByOrderId = findPaymentAmountsByOrderId(TicketListGroupedByOrderIdMap.keySet());
            final Map<String, BigDecimal> refundAmountByOrderId = findRefundAmountsFromPaymentIfAllTicketsCanceled(TicketListGroupedByOrderIdMap);
            final List<UserBuyTicketInfoResponse.Order> orders = convertToOrderList(TicketListGroupedByOrderIdMap, eventMap, ticketTypeMap, paymentAmountByOrderId, refundAmountByOrderId);

            return new UserBuyTicketInfoResponse(orders);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }

    @Transactional
    public void confirmTicketByStaffCode(final String ticketCode, final String checkCodeFromTicket) {
        try {
            final TicketEntity ticketEntity = ticketRetriever.findTicketEntityByTicketCode(ticketCode);
            verifyTicketStatus(ticketEntity.getStatus());

            final TicketType ticketType = findTicketTypeById(ticketEntity.getTicketTypeId());
            verifyTicketDate(ticketType.getTicketStartAt(), ticketType.getTicketEndAt());

            final Event event = findEventById(ticketEntity.getEventId());
            verifyTicketCheckCode(event.getTicketCheckCode(), checkCodeFromTicket);

            ticketUpdater.updateTicketStatus(ticketEntity, TicketStatus.USED);
        } catch (TicketNotFoundException  e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (EventNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    @Transactional
    public void confirmTicketByStaffCamera(final String ticketCode) {
        try {
            final TicketEntity ticketEntity = ticketRetriever.findTicketEntityByTicketCode(ticketCode);
            verifyTicketStatus(ticketEntity.getStatus());

            final TicketType ticketType = findTicketTypeById(ticketEntity.getTicketTypeId());
            verifyTicketDate(ticketType.getTicketStartAt(), ticketType.getTicketEndAt());

            ticketUpdater.updateTicketStatus(ticketEntity, TicketStatus.USED);
        } catch (TicketNotFoundException  e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }

    @Transactional(readOnly = true)
    public DoorValidateUserTicket validateUserTicket(final String ticketCode) {
        try {
            final Ticket ticket = ticketRetriever.findTicketByTicketCode(ticketCode);
            verifyTicketStatus(ticket.getStatus());

            final TicketType ticketType = findTicketTypeById(ticket.getTicketTypeId());
            verifyTicketDate(ticketType.getTicketStartAt(), ticketType.getTicketEndAt());

            final Event event = findEventById(ticket.getEventId());

            return DoorValidateUserTicket.of(event.getName(), ticketType.getTicketTypeName(), ticketType.getTicketStartAt(), ticketType.getTicketEndAt());
        } catch (TicketNotFoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (EventNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    //Redis에 있는 티켓 개수로 isTicketSoldOut 판별
    // 1) Redis 장애(연결 실패/타임아웃 등)일 때만 DB remainTicketCount fallback
    // 2) Redis 값이 null(키 없음) 이면 예외 throw (fallback 금지)
    private Map<Long, Boolean> buildSoldOutMapByTicketTypeId(final List<TicketType> ticketTypeList) {
        if (ticketTypeList == null || ticketTypeList.isEmpty()) {
            return Map.of();
        }

        //장애 fallback 시 사용할 DB remain 맵
        final Map<Long, Integer> dbRemainMap = ticketTypeList.stream()
                .collect(Collectors.toMap(
                        TicketType::getTicketTypeId,
                        TicketType::getRemainTicketCount,
                        (a, b) -> a // 중복이면 앞 값 유지
                ));

        final List<Long> ticketTypeIds = ticketTypeList.stream()
                .map(TicketType::getTicketTypeId)
                .distinct()
                .toList();

        final List<String> ticketTypeKeys = ticketTypeIds.stream()
                .map(this::buildRedisRemainKey)
                .toList();

        final List<String> redisTicketTypeCountValues;
        try {
            redisTicketTypeCountValues = redisManager.mGet(ticketTypeKeys);
        } catch (RedisConnectionFailureException | RedisSystemException | QueryTimeoutException | RedisUnavailableException e) {
            // Redis 장애일 때만 fallback
            log.error("[TicketType 개수 정보 조회] Redis 장애로 DB remainTicketCount fallback 처리. ticketType={}, err={}",
                    ticketTypeIds, e.getClass().getSimpleName());

            final Map<Long, Boolean> fallback = new HashMap<>();
            for (Long id : ticketTypeIds) {
                final long remain = dbRemainMap.getOrDefault(id, 0);
                fallback.put(id, remain <= 0);
            }
            return fallback;
        }

        if (redisTicketTypeCountValues == null || redisTicketTypeCountValues.size() != ticketTypeKeys.size()) {
            log.error("[TicketInfo] Redis mGet 결과 크기 불일치. keysSize={}, valuesSize={}",
                    ticketTypeKeys.size(), redisTicketTypeCountValues == null ? -1 : redisTicketTypeCountValues.size());
            throw new PaymentBadRequestException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
        }

        final Map<Long, Boolean> soldOutMap = new HashMap<>();

        for (int i = 0; i < ticketTypeIds.size(); i++) {
            final long ticketTypeId = ticketTypeIds.get(i);
            final String key = ticketTypeKeys.get(i);
            final String raw = redisTicketTypeCountValues.get(i);

            if (raw == null) {
                log.error("[TicketInfo] Redis key 없음 key={}, ticketTypeId={}", key, ticketTypeId);
                throw new RedisKeyNotFoundException();
            }

            final long remain;
            try {
                remain = Long.parseLong(raw);
            } catch (NumberFormatException e) {
                log.error("[TicketInfo] Redis ticketType value parsing 에러. key={}, raw={}, ticketTypeId={}", key, raw, ticketTypeId);
                throw new PaymentBadRequestException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
            }

            // redis ticketType remain <= 0 이면 soldOut=true
            soldOutMap.put(ticketTypeId, remain <= 0);
        }

        return soldOutMap;
    }

    private String buildRedisRemainKey(final long ticketTypeId) {
        return Constants.REDIS_TICKET_TYPE_KEY_NAME + ticketTypeId + Constants.REDIS_TICKET_TYPE_REMAIN;
    }

    private Event findEventById(final long eventId) {
        return eventRetriever.findEventById(eventId);
    }

    private TicketType findTicketTypeAndVerifyTicketDate(final long ticketTypeId) {
        final TicketType ticketType = findTicketTypeById(ticketTypeId);
        verifyTicketDate(ticketType.getTicketStartAt(), ticketType.getTicketEndAt());
        return ticketType;
    }

    private TicketType findTicketTypeById(final long ticketTypeId) {
        return ticketTypeRetriever.findTicketTypeById(ticketTypeId);
    }

    private void verifyTicketStatus(final TicketStatus ticketStatus) {
        if(ticketStatus == TicketStatus.USED)  {
            throw new ConflictTicketException(ErrorCode.CONFLICT_ALREADY_USED_TICKET);
        } else if(ticketStatus == TicketStatus.CANCELED) {
            throw new IllegalTicketException(ErrorCode.BAD_REQUEST_CANCELED_TICKET);
        }
    }

    private void verifyTicketCheckCode(final String ticketCheckCode, final String checkCodeFromTicket) {
        if(!Objects.equals(ticketCheckCode, checkCodeFromTicket)) {
            throw new IllegalTicketException(ErrorCode.BAD_REQUEST_TICKET_CHECK_CODE_ERROR);
        }
    }

    private void verifyTicketDate(final LocalDateTime ticketStartAt, final LocalDateTime ticketEndAt) {
        final LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(ticketStartAt) || now.isAfter(ticketEndAt)) {
            throw new DateTicketException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        }
    }

    private List<UserBuyTicketInfoResponse.Order> convertToOrderList(final Map<String, List<Ticket>> ticketsGroupedByOrderId,
                                                                     final Map<Long, Event> eventMap,
                                                                     final Map<Long, TicketType> ticketTypeMap,
                                                                     final Map<String, BigDecimal> paymentAmountByOrderId,
                                                                     final Map<String, BigDecimal> refundAmountByOrderId) {
        return ticketsGroupedByOrderId.entrySet().stream()
                .map(entry -> {
                    final String orderId = entry.getKey();
                    final List<Ticket> ticketsInOrder = entry.getValue();
                    final String orderDate = ticketsInOrder.get(0).getCreatedAt()
                            .format(DateTimeFormatter.ofPattern(ORDER_DATE_FORMAT));

                    final long eventId = ticketsInOrder.get(0).getEventId();
                    final String eventName = eventMap.get(eventId).getName();
                    final String eventVenue = eventMap.get(eventId).getVenue();

                    final BigDecimal paymentAmount = paymentAmountByOrderId.get(orderId);
                    final String formattedPaymentPrice = paymentAmount != null ? PriceFormatterUtil.formatPrice(paymentAmount) : null;

                    final List<UserBuyTicketInfoResponse.TicketInfo> ticketInfos = ticketsInOrder.stream()
                            .map(ticket -> {
                                final TicketType ticketType = ticketTypeMap.get(ticket.getTicketTypeId());
                                final boolean expired = isTicketDateExpired(ticketType.getTicketEndAt());

                                return new UserBuyTicketInfoResponse.TicketInfo(
                                        ticket.getTicketCode(),
                                        ticketType.getTicketTypeName(),
                                        toUiStatus(ticket.getStatus(), expired),
                                        LocalDateTimeFormatterUtil.formatStartEndDate(ticketType.getTicketStartAt(), ticketType.getTicketEndAt()),
                                        TimeFormatterUtil.formatEventTime(ticketType.getTicketStartAt(), ticketType.getTicketEndAt())
                                );
                            }).toList();

                    // 한 오더내에서 모든 티켓이 USABLE 상태일 때만 취소 가능함
                    final boolean canCancel = ticketInfos.stream()
                            .allMatch(info -> info.ticketStatus() == UserBuyTicketInfoResponse.TicketStatusForUi.USABLE);

                    final BigDecimal refundAmount = refundAmountByOrderId.get(orderId);
                    final String formattedRefund = refundAmount != null ? PriceFormatterUtil.formatPrice(refundAmount) : null;

                    return new UserBuyTicketInfoResponse.Order(orderDate, orderId, eventName, eventVenue, formattedPaymentPrice, formattedRefund,canCancel, ticketInfos);
                })
                .sorted(Comparator.comparing(UserBuyTicketInfoResponse.Order::orderDate).reversed())
                .toList();
    }

    private boolean isTicketDateExpired(final LocalDateTime endDate) {
        return LocalDateTime.now().isAfter(endDate);
    }

    private UserBuyTicketInfoResponse.TicketStatusForUi toUiStatus(final TicketStatus status, final boolean expired) {
        if (expired && status == TicketStatus.RESERVED) {
            return UserBuyTicketInfoResponse.TicketStatusForUi.EXPIRED;
        }

        return switch (status) {
            case RESERVED -> UserBuyTicketInfoResponse.TicketStatusForUi.USABLE;
            case USED -> UserBuyTicketInfoResponse.TicketStatusForUi.USED;
            case CANCELED -> UserBuyTicketInfoResponse.TicketStatusForUi.REFUNDED;
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
                                                         final Map<Long, Boolean> soldOutByTicketTypeId,
                                                         final LocalDateTime now ) {
        final boolean isAvailable = isRoundAvailable(round, now);
        final List<TicketType> ticketTypeListInMap = Objects.requireNonNull(ticketTypesByTicketRoundIdMap.get(round.getTicketRoundId()));
        final String roundPrice = formatRoundPrice(ticketTypeListInMap);
        final List<EventTicketInfoResponse.TicketType> ticketTypeDtoList = getTicketTypeIfTicketRoundAvailableOrEmptyList(isAvailable, ticketTypeListInMap, soldOutByTicketTypeId);

        return new EventTicketInfoResponse.Round(
                round.getTicketRoundId(),
                isAvailable,
                roundPrice,
                round.getTicketRoundTitle(),
                ticketTypeDtoList
        );
    }

    private boolean isRoundAvailable(final TicketRound round, final LocalDateTime now) {
        return !now.isBefore(round.getSalesStartAt()) && !now.isAfter(round.getSalesEndAt());
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
                                                                                                    final List<TicketType> ticketTypeListInMap,
                                                                                                    final Map<Long, Boolean> soldOutByTicketTypeId) {
        return isAvailable
                ? ticketTypeListInMap.stream()
                .sorted(Comparator.comparing(TicketType::getTicketTypeId))
                .map(ticketType -> makeFormattedTicketTypeDto(ticketType, soldOutByTicketTypeId))
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
    private EventTicketInfoResponse.TicketType makeFormattedTicketTypeDto(final TicketType ticketType,
                                                                          final Map<Long, Boolean> soldOutByTicketTypeId) {
        final String formattedDate = LocalDateTimeFormatterUtil.formatStartEndDate(
                ticketType.getTicketStartAt(), ticketType.getTicketEndAt());
        final String formattedTime = ticketType.getTicketStartAt()
                .toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        final boolean isSoldOut = soldOutByTicketTypeId.getOrDefault(ticketType.getTicketTypeId(), false);

        return new EventTicketInfoResponse.TicketType(
                ticketType.getTicketTypeId(),
                ticketType.getTicketTypeName(),
                formattedDate,
                formattedTime,
                PriceFormatterUtil.formatPrice(ticketType.getTicketPrice()),
                isSoldOut
        );
    }

    private Map<String, BigDecimal> findRefundAmountsFromPaymentIfAllTicketsCanceled(final Map<String, List<Ticket>> ticketsGroupedByOrderId) {
        final Set<String> canceledOrderIds = ticketsGroupedByOrderId.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .allMatch(ticket -> ticket.getStatus() == TicketStatus.CANCELED))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (canceledOrderIds.isEmpty()) {
            return Map.of();
        }

        final List<Payment> paymentEntities = paymentRetriever.findPaymentByOrderIdIn(canceledOrderIds);

        return paymentEntities.stream()
                .collect(Collectors.toMap(
                        Payment::getOrderId,
                        Payment::getTotalAmount
                ));
    }

    private Map<String, BigDecimal> findPaymentAmountsByOrderId(final Set<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }

        final List<Payment> paymentEntities = paymentRetriever.findPaymentByOrderIdIn(orderIds);

        return paymentEntities.stream()
                .collect(Collectors.toMap(
                        Payment::getOrderId,
                        Payment::getTotalAmount
                ));
    }
}
