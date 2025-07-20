package com.permitseoul.permitserver.domain.payment.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class PaymentBadRequestException extends PaymentApiException {
    public PaymentBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
