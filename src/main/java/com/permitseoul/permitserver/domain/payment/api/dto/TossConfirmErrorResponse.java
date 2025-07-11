package com.permitseoul.permitserver.domain.payment.api.dto;

import lombok.Getter;

@Getter
public class TossConfirmErrorResponse {
    private String code;
    private String message;
}
