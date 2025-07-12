package com.permitseoul.permitserver.domain.payment.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmErrorResponse {
    private String code;
    private String message;
}
