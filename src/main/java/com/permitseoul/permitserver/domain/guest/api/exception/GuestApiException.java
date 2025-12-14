package com.permitseoul.permitserver.domain.guest.api.exception;

import com.permitseoul.permitserver.domain.guest.GuestBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class GuestApiException extends GuestBaseException {
    private final ErrorCode errorCode;
}
