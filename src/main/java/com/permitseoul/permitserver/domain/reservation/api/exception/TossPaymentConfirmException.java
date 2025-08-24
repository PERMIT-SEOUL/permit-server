package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.domain.payment.api.exception.PaymentApiException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class TossPaymentConfirmException extends PaymentApiException {
    public TossPaymentConfirmException(ErrorCode errorCode, String tossMessage) {
        super(errorCode);
        this.tossMessage = tossMessage;
    }

    private final String tossMessage;
}
