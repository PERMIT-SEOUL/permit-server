package com.permitseoul.permitserver.domain.user.core.domain;

import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class User {
    private final long userId;
    private final String name;
    private final Gender gender;
    private final int age;
    private final String email;
    private final String socialId;
    private final SocialType socialType;
    private final UserRole userRole;

    public static User fromEntity(final UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getName(),
                userEntity.getGender(),
                userEntity.getAge(),
                userEntity.getEmail(),
                userEntity.getSocialId(),
                userEntity.getSocialType(),
                userEntity.getUserRole()
        );
    }
}
