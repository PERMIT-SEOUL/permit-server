package com.permitseoul.permitserver.domain.user.api.dto;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;

public record UserInfoResponse(
        String name,
        int age,
        Gender gender,
        String email,
        UserRole role
) {
    public static UserInfoResponse of(final String name,
                                      final int age,
                                      final Gender gender,
                                      final String email,
                                      final UserRole role) {
        return new UserInfoResponse(name, age, gender, email, role);
    }
}
