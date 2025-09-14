package com.permitseoul.permitserver.domain.reservation.api.exception;

import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.Getter;

@Getter
public class PaymentFeignException extends ReservationApiException {
    public PaymentFeignException(ErrorCode errorCode, String feignErrorMessage) {
        super(errorCode);
        this.feignErrorMessage = feignErrorMessage;
    }

    private final String feignErrorMessage;
}
