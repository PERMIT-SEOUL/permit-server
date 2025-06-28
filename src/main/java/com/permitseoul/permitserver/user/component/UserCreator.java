package com.permitseoul.permitserver.user.component;

import com.permitseoul.permitserver.user.domain.entity.User;
import com.permitseoul.permitserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {
    private final UserRepository userRepository;

    public User createUser(final User user) {
        return userRepository.save(user);
    }
}
