package com.permitseoul.permitserver.domain.user.core.component;

import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {
    private final UserRepository userRepository;

    public UserEntity createUser(final UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}
