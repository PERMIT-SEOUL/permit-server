package com.permitseoul.permitserver.domain.reservation.api.service;

import com.permitseoul.permitserver.domain.coupon.core.component.CouponRetriever;
import com.permitseoul.permitserver.domain.coupon.core.domain.Coupon;
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
import com.permitseoul.permitserver.domain.reservation.core.domain.Reservation;
import com.permitseoul.permitserver.domain.reservation.core.exception.ReservationNotFoundException;
import com.permitseoul.permitserver.domain.reservationsession.core.component.ReservationSessionRetriever;
import com.permitseoul.permitserver.domain.reservationsession.core.domain.ReservationSession;
import com.permitseoul.permitserver.domain.reservationsession.core.exception.ReservationSessionNotFoundException;
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
import com.permitseoul.permitserver.global.Constants;
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
    private final ReservationSessionRetriever reservationSessionRetriever;

    private static final int COUPON_CAN_BUY_TICKET_MAX_ = 1;

    public String saveReservation(final long userId,
                                  final long eventId,
                                  final String couponCode,
                                  final BigDecimal totalAmount,
                                  final String orderId,
                                  final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos,
                                  final LocalDateTime now) {
        // 요청한 티켓타입 ID 목록 수집
        final List<Long> requestTicketTypeIds = requestTicketTypeInfos.stream()
                .map(ReservationInfoRequest.TicketTypeInfo::id)
                .toList();

        // 요청한 티켓타입 엔티티들
        final Map<Long, TicketTypeEntity> ticketTypeEntityMap;

        // 레디스에서 재고 감소 티켓타입 정보
        final Map<Long, Integer> redisDecreasedTicketTypeInfo;

        try {
            ticketTypeEntityMap = ticketTypeRetriever.findAllTicketTypeEntityByIds(requestTicketTypeIds).stream()
                    .collect(Collectors.toMap(TicketTypeEntity::getTicketTypeId, it -> it));

            validateExistUserById(userId);
            validateExistEventById(eventId);
            final Coupon validatedcoupon = validateCouponCode(couponCode, requestTicketTypeInfos, eventId);
            validateUsableTicketType(ticketTypeEntityMap, eventId, now);
            validateTotalAmount(ticketTypeEntityMap, requestTicketTypeInfos, totalAmount, validatedcoupon);

            // redis로 선점 예약 방식
            redisDecreasedTicketTypeInfo = decreaseRedisTicketCount(requestTicketTypeInfos);

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

        try {
            final String sessionKey = reservationAndReservationTicketFacade.saveReservationWithTicketAndSession(
                    userId,
                    eventId,
                    orderId,
                    totalAmount,
                    couponCode,
                    requestTicketTypeInfos
            );

            return sessionKey;
        } catch (DataIntegrityViolationException e) {
            increaseRedisTicketCount(redisDecreasedTicketTypeInfo);
            throw e;
        } catch (Exception e) {
            increaseRedisTicketCount(redisDecreasedTicketTypeInfo);
            throw new ReservationIllegalException(ErrorCode.INTERNAL_SESSION_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ReservationInfoResponse getReservationInfo(final long userId, final String sessionKey) {
        try {
            final String orderId = getOrderIdWithValidateSessionKey(userId,sessionKey);
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
        } catch (ReservationSessionNotFoundException e) {
            throw new NotfoundReservationException(ErrorCode.NOT_FOUND_RESERVATION_SESSION);
        }
    }

    private void validateTotalAmount(final Map<Long, TicketTypeEntity> ticketTypeEntityMap,
                                     final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos,
                                     final BigDecimal totalAmount,
                                     final Coupon coupon) {

        BigDecimal calculatedAmount = BigDecimal.ZERO;

        for (ReservationInfoRequest.TicketTypeInfo ticketTypeInfo : requestTicketTypeInfos) {
            final TicketTypeEntity ticketTypeEntity = ticketTypeEntityMap.get(ticketTypeInfo.id());
            if (ticketTypeEntity == null) {
                throw new ReservationBadRequestException(ErrorCode.NOT_FOUND_TICKET_TYPE);
            }
            BigDecimal price = ticketTypeEntity.getTicketPrice();
            BigDecimal count = BigDecimal.valueOf(ticketTypeInfo.count());
            calculatedAmount = calculatedAmount.add(price.multiply(count));
        }

        if (coupon != null) {
            final int discountRate = coupon.getDiscountRate();
            final BigDecimal discount = calculatedAmount.multiply(BigDecimal.valueOf(discountRate)).divide(BigDecimal.valueOf(100));
            calculatedAmount = calculatedAmount.subtract(discount);
        }

        if (calculatedAmount.compareTo(totalAmount) != 0) {
            throw new ReservationBadRequestException(ErrorCode.BAD_REQUEST_AMOUNT_MISMATCH);
        }
    }

    private String getOrderIdWithValidateSessionKey(final long userId, final String sessionKey) {
        final LocalDateTime validTime = LocalDateTime.now().minusMinutes(7);
        final ReservationSession reservationSession = reservationSessionRetriever.getValidatedReservationSession(userId, sessionKey, validTime);
        return reservationSession.getOrderId();
    }


    private Map<Long, Integer> decreaseRedisTicketCount(final List<ReservationInfoRequest.TicketTypeInfo> requestTicketTypeInfos) {
        final Map<Long, Integer> redisDecreaseAppliedTicketTypeInfoMap = new HashMap<>();
        try {
            requestTicketTypeInfos.forEach(
                    ticketTypeInfo -> {
                        final String redisKey = Constants.REDIS_TICKET_TYPE_KEY_NAME + ticketTypeInfo.id() + Constants.REDIS_TICKET_TYPE_REMAIN;
                        final Long remain = redisTemplate.opsForValue().decrement(redisKey, ticketTypeInfo.count());

                        redisDecreaseAppliedTicketTypeInfoMap.put(ticketTypeInfo.id(), ticketTypeInfo.count());

                        if (remain == null || remain < 0) {
                            throw new RedisInSufficientTicketException();
                        }
                    }
            );

            return redisDecreaseAppliedTicketTypeInfoMap;
        } catch (RedisInSufficientTicketException e) {
            increaseRedisTicketCount(redisDecreaseAppliedTicketTypeInfoMap);
            throw new TicketTypeInsufficientCountException();
        }
    }

    // redis 롤백처리
    private void increaseRedisTicketCount(final Map<Long, Integer> requestTicketTypeInfoMap) {
        requestTicketTypeInfoMap.forEach(
                (requestTicketTypeId, count) -> {
                    final String redisKey = Constants.REDIS_TICKET_TYPE_KEY_NAME + requestTicketTypeId + Constants.REDIS_TICKET_TYPE_REMAIN;
                    redisTemplate.opsForValue().increment(redisKey, count);
                }
        );
    }

    private void validateExistUserById(final long userId) {
        userRetriever.validExistUserById(userId);
    }

    private void validateExistEventById(final long eventId) {
        eventRetriever.validExistEventById(eventId);
    }

    private void validateUsableTicketType(final Map<Long, TicketTypeEntity> ticketTypeMap, final long eventId, final LocalDateTime now) {
        ticketTypeMap.forEach( (ticketTypeId, ticketTypeEntity) -> {
                    //티켓 구매가능 날짜 검증
                    final TicketRoundEntity ticketRoundEntity = ticketRoundRetriever.findTicketRoundEntityById(ticketTypeEntity.getTicketRoundId());
                    if (ticketRoundEntity.getEventId() != eventId) {
                        throw new TicketRoundNotFoundException();
                    }
                    ticketRoundEntity.verifyTicketSalesAvailable(now);
                }
        );
    }

    private Coupon validateCouponCode(final String couponCode,
                                      final List<ReservationInfoRequest.TicketTypeInfo> ticketTypeInfos,
                                      final long eventId) {
        if(couponCode == null || couponCode.isEmpty()) {
            throw new ReservationBadRequestException(ErrorCode.NOT_FOUND_COUPON_CODE);
        }
        //쿠폰코드쓰면 티켓 구매 1개만 가능함
        if(ticketTypeInfos == null || ticketTypeInfos.size() != COUPON_CAN_BUY_TICKET_MAX_ || ticketTypeInfos.get(0).count() != COUPON_CAN_BUY_TICKET_MAX_) {
            throw new ReservationBadRequestException(ErrorCode.BAD_REQUEST_COUPON_TICKET_COUNT);
        }

        return couponRetriever.findValidCouponByCodeAndEvent(couponCode, eventId);
    }

}
