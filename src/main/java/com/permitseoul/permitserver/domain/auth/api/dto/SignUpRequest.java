package com.permitseoul.permitserver.domain.auth.api.dto;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SignUpRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String userName,

        @Positive(message = "나이는 양수여야 합니다.")
        int userAge,

        @NotNull(message = "성별은 필수입니다.")
        Gender userGender,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String userEmail,

        @NotNull(message = "소셜 타입은 필수입니다.")
        SocialType socialType,

        @NotBlank(message = "socialAccessToken는 필수입니다.")
        String socialAccessToken
) {
}
