package com.permitseoul.permitserver.domain.reservation.api.service;

import com.permitseoul.permitserver.domain.auth.core.jwt.CookieCreatorUtil;
import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponConflictException;
import com.permitseoul.permitserver.domain.coupon.core.exception.CouponNotfoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoRequest;
import com.permitseoul.permitserver.domain.reservation.api.dto.ReservationInfoResponse;
import com.permitseoul.permitserver.domain.reservation.api.exception.*;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationAndReservationTicketFacade;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationRetriever;
import com.permitseoul.permitserver.domain.reservation.core.component.ReservationSaver;
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationticket.core.component.ReservationTicketSaver;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundExpiredException;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeInsufficientCountException;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final EventRetriever eventRetriever;
    private final UserRetriever userRetriever;
    private final TicketTypeRetriever ticketTypeRetriever;
    private final CouponRetriever couponRetriever;
    private final ReservationRetriever reservationRetriever;
    private final TicketRoundRetriever ticketRoundRetriever;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReservationAndReservationTicketFacade reservationAndReservationTicketFacade;

    private static final String REDIS_TICKET_TYPE_KEY_NAME = "ticket_type:";
    private static final String REDIS_TICKET_TYPE_REMAIN = ":remain";
    private static final int COUPON_CAN_BUY_TICKET_MAX_ = 1;


    public String saveReservation(final long userId,
                                  final long eventId,
                                  final String couponCode,
                                  final BigDecimal totalAmount,
                                  final String orderId,
                                  final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Map<Long, Integer> requestedTicketTypeAndCountMap;
        try {
            validExistUserById(userId);
            validExistEventById(eventId);
            validUsableTicketType(requestTicketTypeInfos);

            if(couponCode != null) {
                validateCouponCode(couponCode, requestTicketTypeInfos);
            }

            //redis로 선점 예약 방식 (10분)
            requestedTicketTypeAndCountMap = decreaseRedisTicketCount(requestTicketTypeInfos);

        } catch (EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (UserNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_USER);
        } catch (CouponNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_COUPON_CODE);
        } catch (CouponConflictException e) {
            throw new ConflictReservationException(ErrorCode.CONFLICT_ALREADY_USED_COUPON_CODE);
        } catch (TicketRoundExpiredException e) {
            throw new ExpiredReservationException(ErrorCode.BAD_REQUEST_TICKET_SALES_EXPIRED);
        } catch (TicketRoundNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        } catch (TicketTypeInsufficientCountException e) {
            throw new InSufficientReservationException(ErrorCode.CONFLICT_INSUFFICIENT_TICKET);
        } catch (IllegalStateException e) {
            throw new ReservationBadRequestException(ErrorCode.BAD_REQUEST_TICKET_TYPE_DUPLICATED);
        }

        final String sessionKey;
        try {
            sessionKey = reservationAndReservationTicketFacade.saveReservationWithTicketAndSessionKey(
                    userId,
                    eventId,
                    orderId,
                    totalAmount,
                    couponCode,
                    requestTicketTypeInfos
            );
            return sessionKey;
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            increaseRedisTicketCount(requestedTicketTypeAndCountMap);
            throw new ReservationIllegalException(ErrorCode.INTERNAL_SESSION_ERROR);
        }
    }

    private Map<Long, Integer> decreaseRedisTicketCount(final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Map<Long, Integer> requestTicketTypeInfoMap = new HashMap<>();
        try {
            requestTicketTypeInfos.forEach(
                    ticketTypeInfo -> {
                        final String redisKey = REDIS_TICKET_TYPE_KEY_NAME + ticketTypeInfo.id() + REDIS_TICKET_TYPE_REMAIN;
                        final Long remain = redisTemplate.opsForValue().decrement(redisKey, ticketTypeInfo.count());

                        requestTicketTypeInfoMap.put(ticketTypeInfo.id(), ticketTypeInfo.count());

                        if (remain == null || remain < 0) {
                            throw new RedisInSufficientTicketException();
                        }
                    }
            );

            return requestTicketTypeInfoMap;
        } catch (RedisInSufficientTicketException e) { //개수 부족하면 redis 롤백 처리
            requestTicketTypeInfoMap.forEach(
                    (requestTicketTypeId, count) -> {
                        final String redisKey = REDIS_TICKET_TYPE_KEY_NAME + requestTicketTypeId + REDIS_TICKET_TYPE_REMAIN;
                        redisTemplate.opsForValue().increment(redisKey, count);
                    });
            throw new TicketTypeInsufficientCountException();
        }
    }

    //session 저장 실패하면 redis 롤백처리
    private void increaseRedisTicketCount(final Map<Long, Integer> requestTicketTypeInfoMap) {
            requestTicketTypeInfoMap.forEach(
                    (requestTicketTypeId, count) -> {
                        final String redisKey = REDIS_TICKET_TYPE_KEY_NAME + requestTicketTypeId + REDIS_TICKET_TYPE_REMAIN;
                        redisTemplate.opsForValue().increment(redisKey, count);
                    }
            );
    }

    @Transactional(readOnly = true)
    public ReservationInfoResponse getReservationInfo(final long userId, final String orderId) {
        try {
            final User user = userRetriever.findUserById(userId);
            final Reservation reservation = reservationRetriever.findReservationByOrderIdAndUserId(orderId, userId);
            final Event event = eventRetriever.findEventById(reservation.getEventId());

            return ReservationInfoResponse.of(
                    event.getName(),
                    reservation.getOrderId(),
                    user.getName(),
                    user.getEmail(),
                    reservation.getTotalAmount(),
                    user.getSocialId()
            );
        } catch (EventNotfoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_EVENT);
        } catch (UserNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_USER);
        } catch (ReservationNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION);
        }
    }

    private void validExistUserById(final long userId) {
        userRetriever.validExistUserById(userId);
    }

    private void validExistEventById(final long eventId) {
        eventRetriever.validExistEventById(eventId);
    }

    private void validUsableTicketType(final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Map<Long, Integer> ticketCountMap = requestTicketTypeInfos.stream()
                .collect(Collectors.toMap(ReservationInfoRequest.TicketTypeInfo::id, ReservationInfoRequest.TicketTypeInfo::count));

        ticketCountMap.forEach((ticketTypeId, requestedCount) -> {
                    final TicketTypeEntity ticketType = ticketTypeRetriever.findTicketTypeEntityById(ticketTypeId);
                    // 티켓 개수 검증
                    ticketType.verifyTicketCount(requestedCount);
                    //티켓 구매가능 날짜 검증
                    final TicketRoundEntity ticketRound = ticketRoundRetriever.findTicketRoundEntityById(ticketType.getTicketRoundId());
                    final LocalDateTime now = LocalDateTime.now();
                    ticketRound.verifyTicketSalesAvailable(now);
                }
        );
    }

    private void validateCouponCode(final String couponCode, final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos) {
        couponRetriever.isExistCoupon(couponCode);
        couponRetriever.isCouponValid(couponCode);
        //쿠폰코드쓰면 티켓 구매 1개만 가능함
        if (ticketTypeInfos == null || ticketTypeInfos.size() != COUPON_CAN_BUY_TICKET_MAX_ || ticketTypeInfos.get(0).count() != COUPON_CAN_BUY_TICKET_MAX_) {
            throw new ReservationBadRequestException(ErrorCode.BAD_REQUEST_COUPON_TICKET_COUNT);
        }
    }
}
