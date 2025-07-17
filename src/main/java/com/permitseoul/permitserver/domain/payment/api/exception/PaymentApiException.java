package com.permitseoul.permitserver.domain.payment.api.exception;

import com.permitseoul.permitserver.domain.payment.PaymentBaseException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class PaymentApiException extends PaymentBaseException {
  private final ErrorCode errorCode;
}
