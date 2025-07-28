package com.permitseoul.permitserver.domain.user.core.component;

import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import com.permitseoul.permitserver.domain.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserRetriever {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public long getUserIdBySocialInfo(final SocialType socialType, final String socialId) {
        return userRepository.findUserIdBySocialTypeAndSocialId(socialType, socialId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void validDuplicatedUserBySocial(final SocialType socialType, final String socialId) {
        if (userRepository.existsBySocialTypeAndSocialId(socialType, socialId)) {
            throw new UserDuplicateException();
        }
    }

    @Transactional(readOnly = true)
    public void validExistUserById(final long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public User findUserById(final long userId) {
        final UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return User.fromEntity(userEntity);
    }

    @Transactional(readOnly = true)
    public void validEmailDuplicated(final String userEmail) {
        if (userRepository.existsByEmail(userEmail)) {
            throw new UserDuplicateException();
        }
    }
}
