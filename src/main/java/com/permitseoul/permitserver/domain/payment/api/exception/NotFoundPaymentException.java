package com.permitseoul.permitserver.domain.payment.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;

public class NotFoundPaymentException extends PaymentApiException {
    public NotFoundPaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
