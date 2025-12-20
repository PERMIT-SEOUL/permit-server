package com.permitseoul.permitserver.domain.user.api.dto;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserInfoRequest(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @Email(message = "이메일 형식이 아닙니다.")
        String email
) {
}
