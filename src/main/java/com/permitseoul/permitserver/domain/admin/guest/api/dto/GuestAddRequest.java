package com.permitseoul.permitserver.domain.admin.guest.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GuestAddRequest(
        @NotBlank(message = "게스트 이름이 공백입니다.")
        String guestName,

        @NotBlank(message = "게스트 타입이 공백입니다.")
        String guestType,

        @NotBlank(message = "게스트 소속이 공백입니다.")
        String affiliation,

        @Nullable
        String phoneNumber,

        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "게스트 이메일이 공백입니다.")
        String email
) {
}
