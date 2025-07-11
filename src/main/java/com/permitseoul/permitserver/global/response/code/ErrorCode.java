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
    UNAUTHORIZED_FEIGN(HttpStatus.UNAUTHORIZED, 40108, "feign 오류입니다."),

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

    /**
     * 405 Method Not Allowed
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "잘못된 HTTP method 요청입니다."),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT, 40900, "이미 존재하는 리소스입니다."),
    INTEGRITY_CONFLICT(HttpStatus.CONFLICT, 40901, "데이터 무결성 위반입니다."),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류입니다."),
    INTERNAL_RT_CACHE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "RT가 캐시에 저장되어 있지 않습니다."),
    INTERNAL_JSON_FORMAT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "RT가 캐시에 저장되어 있지 않습니다."),



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
