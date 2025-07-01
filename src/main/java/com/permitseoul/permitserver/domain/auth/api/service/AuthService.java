package com.permitseoul.permitserver.domain.auth.api.service;


import com.permitseoul.permitserver.domain.auth.core.domain.Token;
import com.permitseoul.permitserver.domain.auth.api.dto.TokenDto;
import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthFeignException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthRTCacheException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.domain.auth.core.jwt.RTCacheManager;
import com.permitseoul.permitserver.domain.auth.core.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.domain.user.api.exception.UserUnAuthorizedException;
import com.permitseoul.permitserver.domain.user.api.exception.UserNotFoundApiException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.domain.user.core.component.UserCreator;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.Sex;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserExistException;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
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
    private final RTCacheManager rtCacheManager;

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
            final UserEntity newUserEntity = createUser(userName, userAge, userSex, userEmail, userSocialId, socialType);
            final Token newToken = GetJwtToken(newUserEntity.getUserId());
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthFeignException e) {
            throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN, e.getMessage());
        } catch (UserExistException e ) {
            throw new UserUnAuthorizedException(ErrorCode.CONFLICT);
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
                    .orElseThrow(() -> new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN)
                    );
            final long userId = getUserIdIfUserExist(socialType, userSocialInfoDto.userSocialId());
            final Token newToken = GetJwtToken(userId);
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthFeignException e) {
            throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN, e.getMessage());
        } catch (UserNotFoundException e ) {
            throw new UserNotFoundApiException(ErrorCode.NOT_FOUND_USER, socialAccessToken);
        }
    }

    //jwt 재발급
    public TokenDto reissue(final String refreshToken) {
        try {
            final long userId = jwtProvider.extractUserIdFromToken(refreshToken);
            final String refreshTokenFromCache = rtCacheManager.getRefreshTokenFromCache(userId);
            if (!refreshToken.equals(refreshTokenFromCache)) {
                throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
            }
            final Token newToken = GetJwtToken(userId);
            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthWrongJwtException e) {
            throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
        } catch (ExpiredJwtException e) {
            throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_RT_EXPIRED);
        } catch (AuthRTCacheException e) {
            throw new UserUnAuthorizedException(ErrorCode.INTERNAL_RT_CACHE_ERROR);
        }
    }

    //로그아웃
    public void logout(final long userId, final String refreshTokenFromCookie) {
        final String refreshTokenFromCache = rtCacheManager.getRefreshTokenFromCache(userId);
        if (!refreshTokenFromCookie.equals(refreshTokenFromCache)) {
            throw new UserUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
        }
        rtCacheManager.deleteRefreshTokenFromCache(userId);
    }

    private String getUserSocialId(final SocialType socialType, final String socialAccessToken) {
        return loginStrategyManager.getStrategy(socialType).getUserSocialId(socialAccessToken);
    }

    private UserEntity createUser(final String userName,
                                  final int userAge,
                                  final Sex userSex,
                                  final String userEmail,
                                  final String userSocialId,
                                  final SocialType socialType
    ) {
        final UserEntity newUserEntity = UserEntity.create(
                userName,
                userSex,
                userAge,
                userEmail,
                userSocialId,
                socialType,
                UserRole.USER);
        return userCreator.createUser(newUserEntity);
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
