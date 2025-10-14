package com.permitseoul.permitserver.global.response.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode implements ApiCode {
    /**
     * 400 Bad Request
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다."),
    BAD_REQUEST_REQUEST_BODY_VALID(HttpStatus.BAD_REQUEST, 40001, "request body 검증 실패입니다."),
    BAD_REQUEST_REQUEST_PARAM_MODELATTRI(HttpStatus.BAD_REQUEST, 40002, "request param 혹은 modelattribute 검증 실패입니다."),
    BAD_REQUEST_MISSING_PARAM(HttpStatus.BAD_REQUEST, 40003, "필수 param이 없습니다."),
    BAD_REQUEST_METHOD_ARGUMENT_TYPE(HttpStatus.BAD_REQUEST, 40004, "메서드 인자타입이 잘못되었습니다."),
    BAD_REQUEST_NOT_READABLE(HttpStatus.BAD_REQUEST, 40005, "json 오류 혹은 reqeust body 필드 오류 입니다."),
    BAD_REQUEST_COUPON_TICKET_COUNT(HttpStatus.BAD_REQUEST, 40006, "쿠폰코드로는 하나의 티켓만 구매 가능합니다."),
    BAD_REQUEST_TICKET_SALES_EXPIRED(HttpStatus.BAD_REQUEST, 40007, "구매 날짜가 아닌 티켓입니다."),
    BAD_REQUEST_TICKET_TYPE_DUPLICATED(HttpStatus.BAD_REQUEST, 40008, "같은 티켓타입 아이디 여러 개가 요청되었습니다."),
    BAD_REQUEST_SESSION_ORDER_ID(HttpStatus.BAD_REQUEST, 40009, "예약 세션에 있는 orderId와 요청하신 orderId가 다릅니다."),
    BAD_REQUEST_TICKET_COUNT_ZERO(HttpStatus.BAD_REQUEST, 40010, "구매하려는 티켓 개수가 1보다 작습니다."),
    BAD_REQUEST_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, 40011, "구매하려는 티켓들의 가격과 totalAmount가 다릅니다."),
    BAD_REQUEST_ID_DECODE_ERROR(HttpStatus.BAD_REQUEST, 40012, "해당 객체 id를 decode 할 수 없습니다."),
    BAD_REQUEST_DATE_TIME_ERROR(HttpStatus.BAD_REQUEST, 40013, "잘못된 date, time입니다."),
    BAD_REQUEST_TICKET_CHECK_CODE_ERROR(HttpStatus.BAD_REQUEST, 40014, "잘못된 ticket check code 입니다."),
    BAD_REQUEST_CANCELED_TICKET(HttpStatus.BAD_REQUEST, 40015, "취소된 ticket 입니다."),
    BAD_REQUEST_MISMATCH_TICKET_TYPE_ROUND(HttpStatus.BAD_REQUEST, 40016, "ticketType의 roundId와 다른 ticketRoundId 입니다."),



    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "리소스 접근 인증 권한이 없습니다."),
    UNAUTHORIZED_WRONG_AT(HttpStatus.UNAUTHORIZED, 40101, "잘못된 액세스 토큰입니다."),
    UNAUTHORIZED_WRONG_RT(HttpStatus.UNAUTHORIZED, 40102, "잘못된 리프레시 토큰입니다."),
    UNAUTHORIZED_AT_EXPIRED(HttpStatus.UNAUTHORIZED, 40103, "만료된 액세스 토큰입니다."),
    UNAUTHORIZED_RT_EXPIRED(HttpStatus.UNAUTHORIZED, 40104, "만료된 리프레시 토큰입니다."),
    UNAUTHORIZED_DIFF_USER_ID(HttpStatus.UNAUTHORIZED, 40105, "리프레시 토큰 userId와 다른 userId 입니다"),
    UNAUTHORIZED_SECURITY_ENTRY(HttpStatus.UNAUTHORIZED, 40106, "시큐리티 필터 혹은 SecurityContext 오류입니다."),
    UNAUTHORIZED_FEIGN(HttpStatus.UNAUTHORIZED, 40107, "auth feign 오류입니다."),
    UNAUTHORIZED_CANCEL_PAYMENT(HttpStatus.UNAUTHORIZED, 40108, "결제 취소 userID 인증 오류입니다."),
    UNAUTHORIZED_ADMIN_ACCESS_CODE(HttpStatus.UNAUTHORIZED, 40109, "Admin 접근 코드 인증 오류입니다."),
    UNAUTHORIZED_PRINCIPLE(HttpStatus.UNAUTHORIZED, 40110, "UNAUTHORIZED_PRINCIPLE 오류입니다."),
    UNAUTHORIZED_USERID_RESOLVER(HttpStatus.UNAUTHORIZED, 40111, "userID Resolver 오류입니다."),



    /**
     * 403 Forbidden
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "리소스 접근 인가 권한이 없습니다."),

    /**
     * 404 Not Found
     */
    NOT_FOUND_ENTITY(HttpStatus.NOT_FOUND, 40400, "대상을 찾을 수 없습니다."),
    NOT_FOUND_API(HttpStatus.NOT_FOUND, 40401, "잘못된 API입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40402, "없는 유저입니다."),
    NOT_FOUND_AT_COOKIE(HttpStatus.NOT_FOUND, 40403, "요청 accessToken 쿠키가 없습니다."),
    NOT_FOUND_RT_COOKIE(HttpStatus.NOT_FOUND, 40404, "요청 refresh 쿠키가 없습니다."),
    NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, 40405, "해당 아이디 이벤트가 없습니다."),
    NOT_FOUND_RESERVATION(HttpStatus.NOT_FOUND, 40406, "해당 Reservation 이 없습니다."),
    NOT_FOUND_TICKET_TYPE(HttpStatus.NOT_FOUND, 40407, "해당 ticketType 이 없습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, 40408, "해당 payment 가 없습니다."),
    NOT_FOUND_TICKET(HttpStatus.NOT_FOUND, 40409, "해당 ticket 이 없습니다."),
    NOT_FOUND_COUPON_CODE(HttpStatus.NOT_FOUND, 40410, "해당 couponCode 가 없습니다."),
    NOT_FOUND_TICKET_ROUND(HttpStatus.NOT_FOUND, 40411, "해당 티켓 차수(round)가 없습니다."),
    NOT_FOUND_RESERVATION_TICKET(HttpStatus.NOT_FOUND, 40412, "예약된 티켓이 없습니다."),
    NOT_FOUND_RESERVATION_SESSION(HttpStatus.NOT_FOUND, 40413, "예약 세션이 없습니다."),
    NOT_FOUND_RESERVATION_SESSION_AFTER_PAYMENT_SUCCESS(HttpStatus.NOT_FOUND, 40414, "결제 완료 후, 예약 세션이 없습니다."),
    NOT_FOUND_RESERVATION_SESSION_COOKIE(HttpStatus.NOT_FOUND, 40415, "세션 쿠키가 없습니다."),
    NOT_FOUND_EVENT_IMAGE(HttpStatus.NOT_FOUND, 40416, "해당 이벤트 이미지가 없습니다."),
    NOT_FOUND_TICKET_TYPE_PRICE(HttpStatus.NOT_FOUND, 40417, "해당 티켓 라운드의 티켓타입 가격이 없습니다."),
    NOT_FOUND_TIMETABLE_STAGE(HttpStatus.NOT_FOUND, 40418, "타임테이블 장소를 찾을 수 없습니다."),
    NOT_FOUND_TIMETABLE_CATEGORY(HttpStatus.NOT_FOUND, 40419, "타임테이블 카테고리를 찾을 수 없습니다."),
    NOT_FOUND_TIMETABLE(HttpStatus.NOT_FOUND, 40420, "타임테이블을 찾을 수 없습니다."),
    NOT_FOUND_TIMETABLE_BLOCK(HttpStatus.NOT_FOUND, 40421, "타임테이블 블록을 찾을 수 없습니다."),
    NOT_FOUND_TIMETABLE_CATEGORY_COLOR(HttpStatus.NOT_FOUND, 40422, "타임테이블 카테고리 색깔을 찾을 수 없습니다."),
    NOT_FOUND_GUEST(HttpStatus.NOT_FOUND, 40423, "게스트를 찾을 수 없습니다."),
    NOT_FOUND_GUEST_TICKET(HttpStatus.NOT_FOUND, 40424, "게스트 티켓을 찾을 수 없습니다."),
    NOT_FOUND_TIMETABLE_USER_LIKE(HttpStatus.NOT_FOUND, 40425, "유저 좋아요 타임테이블을 찾을 수 없습니다."),
    NOT_FOUND_NOTION_RELATION_ID(HttpStatus.NOT_FOUND, 40426, "Notion Relation Id를 찾을 수 없습니다."),




    /**
     * 405 Method Not Allowed
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "잘못된 HTTP method 요청입니다."),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT, 40900, "이미 존재하는 리소스입니다."),
    INTEGRITY_CONFLICT(HttpStatus.CONFLICT, 40901, "데이터 무결성 위반입니다."),
    CONFLICT_INSUFFICIENT_TICKET(HttpStatus.CONFLICT, 40902, "구매하려는 티켓의 티켓 개수가 부족합니다."),
    CONFLICT_ALREADY_USED_COUPON_CODE(HttpStatus.CONFLICT, 40903, "이미 사용한 쿠폰코드입니다."),
    CONFLICT_USER_EMAIL(HttpStatus.CONFLICT, 40904, "이미 존재하는 이메일입니다."),
    CONFLICT_TIMETABLE_USER_LIKE(HttpStatus.CONFLICT, 40905, "이미 존재하는 타임테이블 유저 좋아요입니다."),
    CONFLICT_ALREADY_USED_TICKET(HttpStatus.CONFLICT, 40906, "이미 사용한 티켓입니다."),



    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류입니다."),
    INTERNAL_RT_CACHE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "RT가 캐시에 저장되어 있지 않습니다."),
    INTERNAL_JSON_FORMAT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "json 포맷팅 과정에서 에러가 발생했습니다."),
    INTERNAL_TICKET_ALGORITHM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "티켓 코드 생성 알고리즘 에러입니다."),
    INTERNAL_PAYMENT_FEIGN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50004, "결제 feign 통신 에러입니다."),
    INTERNAL_ISO_DATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50005, "Toss iso date string에서 localdate로 변환 과정 에러입니다."),
    INTERNAL_TRANSITION_ENUM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50006, "enum status 변환 과정 에러입니다."),
    INTERNAL_SESSION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50007, "reservation session 저장 과정 에러입니다."),
    INTERNAL_ID_ENCODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50008, "객체 guestId Encoding 에러입니다."),
    INTERNAL_TIME_FORMAT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50009, "time format 에러입니다."),
    INTERNAL_QRCODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50010, "qrCode 생성 에러입니다."),
    INTERNAL_EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50011, "이메일 전송 에러입니다."),
    INTERNAL_FILTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50012, "filter 에러입니다."),
    INTERNAL_RT_REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50013, "refreshToken redis 에러입니다."),

    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
