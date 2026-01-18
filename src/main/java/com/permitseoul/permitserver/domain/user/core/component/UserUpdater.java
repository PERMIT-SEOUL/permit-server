package com.permitseoul.permitserver.domain.user.core.component;

import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserUpdater {

    public void updateUserInfo(final UserEntity userEntity,
                               final String name,
                               final Gender gender,
                               final String email) {
        userEntity.updateUserInfo(name, gender, email);
    }

    public void updateUserRole(final UserEntity userEntity,
                               final UserRole userRole) {
        userEntity.updateUserRole(userRole);
    }
}
