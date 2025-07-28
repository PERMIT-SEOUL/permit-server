package com.permitseoul.permitserver.domain.user.api.dto;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;

public record UserInfoResponse(
        String name,
        int age,
        Gender gender,
        String email
) {
    public static UserInfoResponse of(final String name,
                                      final int age,
                                      final Gender gender,
                                      final String email) {
        return new UserInfoResponse(name, age, gender, email);
    }
}
