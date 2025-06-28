package com.permitseoul.permitserver.auth.service;


import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.dto.CookieDto;
import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.auth.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.user.component.UserCreator;
import com.permitseoul.permitserver.user.domain.Sex;
import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.domain.UserRole;
import com.permitseoul.permitserver.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final LoginStrategyManager loginStrategyManager;
    private final UserCreator userCreator;
    private final JwtProvider jwtProvider;

    @Transactional
    public CookieDto signUp(final String userName,
                            final int userAge,
                            final Sex userSex,
                            final String userEmail,
                            final SocialType socialType,
                            final String authorizationCode,
                            final String redirectUrl) {
        final UserSocialInfoDto userSocialInfoDto =  loginStrategyManager.getStrategy(socialType).getUserSocialInfo(authorizationCode, redirectUrl);
        final User newUser = createUser(userName, userAge, userSex, userEmail, userSocialInfoDto);
        final Token newToken = jwtProvider.issueToken(newUser.getUserId(), UserRole.USER);
        return CookieDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
    }

    private User createUser(final String userName,
                            final int userAge,
                            final Sex userSex,
                            final String userEmail,
                            final UserSocialInfoDto userSocialInfoDto
                            ) {
        final User newUser = User.create(
                userName,
                userSex,
                userAge,
                userEmail,
                userSocialInfoDto.userSocialId(),
                userSocialInfoDto.socialType(),
                UserRole.USER);
        return userCreator.createUser(newUser);
    }
}
