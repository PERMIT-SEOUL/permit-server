package com.permitseoul.permitserver.domain.admin.guestticket.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminGuestTicketApiException extends RuntimeException {
  private final ErrorCode errorCode;
}
