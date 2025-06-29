package com.permitseoul.permitserver.auth.service;


import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.dto.TokenDto;
import com.permitseoul.permitserver.auth.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.auth.exception.AuthFeignException;
import com.permitseoul.permitserver.auth.exception.AuthRTCacheException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.auth.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.global.exception.PermitUnAuthorizedException;
import com.permitseoul.permitserver.global.exception.PermitUserNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.user.component.UserCreator;
import com.permitseoul.permitserver.user.component.UserRetriever;
import com.permitseoul.permitserver.user.domain.Sex;
import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.domain.UserRole;
import com.permitseoul.permitserver.user.domain.entity.User;
import com.permitseoul.permitserver.user.exception.UserExistException;
import com.permitseoul.permitserver.user.exception.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final LoginStrategyManager loginStrategyManager;
    private final UserCreator userCreator;
    private final JwtProvider jwtProvider;
    private final UserRetriever userRetriever;

    //회원가입
    @Transactional
    public TokenDto signUp(final String userName,
                           final int userAge,
                           final Sex userSex,
                           final String userEmail,
                           final SocialType socialType,
                           final String socialAccessToken) {
        try {
            final String userSocialId = getUserSocialId(socialType, socialAccessToken);
            isUserExist(socialType, userSocialId);
            final User newUser = createUser(userName, userAge, userSex, userEmail, userSocialId, socialType);
            final Token newToken = GetJwtToken(newUser.getUserId());
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthFeignException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN, e.getMessage());
        } catch (UserExistException e ) {
            throw new PermitUnAuthorizedException(ErrorCode.CONFLICT);
        }
    }

    //로그인
    public TokenDto login(final SocialType socialType, final String authorizationCode, final String redirectUrl) {
        String socialAccessToken = "";
        try {
            final UserSocialInfoDto userSocialInfoDto = getUserSocialInfo(socialType, authorizationCode, redirectUrl);
            socialAccessToken = Optional.ofNullable(
                            userSocialInfoDto.socialAccessToken())
                    .filter(token -> !token.isBlank())
                    .orElseThrow(() -> new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN)
                    );
            final long userId = getUserIdIfUserExist(socialType, userSocialInfoDto.userSocialId());
            final Token newToken = GetJwtToken(userId);
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthFeignException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN, e.getMessage());
        } catch (UserNotFoundException e ) {
            throw new PermitUserNotFoundException(ErrorCode.NOT_FOUND_USER, socialAccessToken);
        }
    }

    //jwt 재발급
    public TokenDto reissue(final String refreshToken) {
        try {
            final long userId = jwtProvider.extractUserIdFromToken(refreshToken);
            final String refreshTokenFromCache = jwtProvider.getRefreshTokenFromCache(userId);
            if (!refreshToken.equals(refreshTokenFromCache)) {
                throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
            }
            final Token newToken = GetJwtToken(userId);
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthWrongJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
        } catch (ExpiredJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_RT_EXPIRED);
        } catch (AuthRTCacheException e) {
            throw new PermitUnAuthorizedException(ErrorCode.INTERNAL_RT_CACHE_ERROR);
        }
    }

    private String getUserSocialId(final SocialType socialType, final String socialAccessToken) {
        return loginStrategyManager.getStrategy(socialType).getUserSocialId(socialAccessToken);
    }

    private User createUser(final String userName,
                            final int userAge,
                            final Sex userSex,
                            final String userEmail,
                            final String userSocialId,
                            final SocialType socialType
    ) {
        final User newUser = User.create(
                userName,
                userSex,
                userAge,
                userEmail,
                userSocialId,
                socialType,
                UserRole.USER);
        return userCreator.createUser(newUser);
    }

    private UserSocialInfoDto getUserSocialInfo(final SocialType socialType,
                                                final String authorizationCode,
                                                final String redirectUrl) {
        return loginStrategyManager.getStrategy(socialType).getUserSocialInfo(authorizationCode, redirectUrl);
    }

    private long getUserIdIfUserExist(final SocialType socialType, final String socialId) {
        return userRetriever.getUserIdBySocialInfo(socialType, socialId);
    }

    private void isUserExist(final SocialType socialType, final String socialId) {
        userRetriever.isExistUser(socialType, socialId);
    }

    private Token GetJwtToken(final long userId) {
        return jwtProvider.issueToken(userId, UserRole.USER);
    }
}
