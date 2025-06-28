package com.permitseoul.permitserver.user.component;

import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.exception.UserExistException;
import com.permitseoul.permitserver.user.exception.UserNotFoundException;
import com.permitseoul.permitserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRetriever {
    private final UserRepository userRepository;

    public long getUserIdBySocialInfo(final SocialType socialType, final String socialId) {
        return userRepository.findUserIdBySocialTypeAndSocialId(socialType, socialId)
                .orElseThrow(UserNotFoundException::new);
    }

    public void isExistUser(final SocialType socialType, final String socialId) {
        if (userRepository.existsBySocialTypeAndSocialId(socialType, socialId)) {
            throw new UserExistException();
        }
    }
}
