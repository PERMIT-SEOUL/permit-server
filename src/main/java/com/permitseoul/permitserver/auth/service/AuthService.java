package com.permitseoul.permitserver.auth.service;


import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.dto.TokenDto;
import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.auth.exception.AuthFeignException;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.auth.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.global.exception.PermitUnAuthorizedException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
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
    public TokenDto signUp(final String userName,
                           final int userAge,
                           final Sex userSex,
                           final String userEmail,
                           final SocialType socialType,
                           final String authorizationCode,
                           final String redirectUrl) {
        try {
            final UserSocialInfoDto userSocialInfoDto = loginStrategyManager.getStrategy(socialType).getUserSocialInfo(authorizationCode, redirectUrl);
            final User newUser = createUser(userName, userAge, userSex, userEmail, userSocialInfoDto);
            final Token newToken = jwtProvider.issueToken(newUser.getUserId(), UserRole.ROLE_USER);
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthFeignException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN);
        } catch (Exception e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED);
        }
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
                UserRole.ROLE_USER);
        return userCreator.createUser(newUser);
    }
}
