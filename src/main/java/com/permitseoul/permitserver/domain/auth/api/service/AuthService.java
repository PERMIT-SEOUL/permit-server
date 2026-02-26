package com.permitseoul.permitserver.domain.auth.api.service;


import com.permitseoul.permitserver.domain.auth.api.exception.AuthRedisException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthUnAuthorizedException;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthUnAuthorizedFeignException;
import com.permitseoul.permitserver.domain.auth.core.domain.Token;
import com.permitseoul.permitserver.domain.auth.api.dto.TokenDto;
import com.permitseoul.permitserver.domain.auth.core.dto.UserSocialInfoDto;
import com.permitseoul.permitserver.domain.auth.core.exception.*;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProperties;
import com.permitseoul.permitserver.domain.auth.core.jwt.RefreshTokenManager;
import com.permitseoul.permitserver.domain.user.core.domain.User;
import com.permitseoul.permitserver.domain.user.core.exception.UserDuplicateException;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.domain.auth.core.strategy.LoginStrategyManager;
import com.permitseoul.permitserver.domain.auth.api.exception.AuthSocialNotFoundApiException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.domain.user.core.component.UserSaver;
import com.permitseoul.permitserver.domain.user.core.component.UserRetriever;
import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import com.permitseoul.permitserver.domain.user.core.domain.entity.UserEntity;
import com.permitseoul.permitserver.domain.user.core.exception.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final LoginStrategyManager loginStrategyManager;
    private final UserSaver userSaver;
    private final JwtProvider jwtProvider;
    private final UserRetriever userRetriever;
    private final RefreshTokenManager refreshTokenManager;
    private final JwtProperties jwtProperties;

    //회원가입
    @Transactional
    public TokenDto signUp(final String userName,
                           final int userAge,
                           final Gender userGender,
                           final String userEmail,
                           final SocialType socialType,
                           final String socialAccessToken) {
        try {
            final String userSocialId = getUserSocialId(socialType, socialAccessToken);
            validDuplicatedUserBySocial(socialType, userSocialId);

            final UserEntity newUserEntity = createUser(userName, userAge, userGender, userEmail, userSocialId, socialType);
            final Token newToken = getSignUpJwtToken(newUserEntity.getUserId());

            saveRefreshTokenInRedis(newUserEntity.getUserId(), newToken.getRefreshToken());

            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthPlatformFeignException e) {
            throw new AuthUnAuthorizedFeignException(ErrorCode.UNAUTHORIZED_FEIGN, e.getPlatformErrorCode());
        } catch (UserDuplicateException e ) {
            throw new AuthUnAuthorizedException(ErrorCode.CONFLICT);
        } catch (AuthRTException e) {
            throw new AuthRedisException(ErrorCode.INTERNAL_RT_REDIS_ERROR);
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
                    .orElseThrow(() -> new AuthUnAuthorizedException(ErrorCode.UNAUTHORIZED_FEIGN)
                    );

            final User user = getUserBySocialInfo(socialType, userSocialInfoDto.userSocialId());
            final Token newToken = getLoginOrReissueJwtToken(user.getUserId(), user.getUserRole());

            saveRefreshTokenInRedis(user.getUserId(), newToken.getRefreshToken());

            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthPlatformFeignException e) {
            throw new AuthUnAuthorizedFeignException(ErrorCode.UNAUTHORIZED_FEIGN, e.getPlatformErrorCode());
        } catch (UserNotFoundException e ) {
            throw new AuthSocialNotFoundApiException(ErrorCode.NOT_FOUND_USER, socialAccessToken);
        } catch (AuthRTException e) {
            throw new AuthRedisException(ErrorCode.INTERNAL_RT_REDIS_ERROR);
        }
    }

    //jwt 재발급
    public TokenDto reissue(final String refreshToken) {
        try {
            final long userId = jwtProvider.extractUserIdFromToken(refreshToken);
            final UserRole userRole = UserRole.valueOf(jwtProvider.extractUserRoleFromToken(refreshToken));
           checkIsSameRefreshToken(userId, refreshToken);

            final Token newToken = getLoginOrReissueJwtToken(userId, userRole);

            saveRefreshTokenInRedis(userId, newToken.getRefreshToken());

            return TokenDto.of(newToken.getAccessToken(), newToken.getRefreshToken());
        } catch (AuthWrongJwtException | AuthRTNotFoundException e) {
            throw new AuthUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
        } catch (AuthExpiredJwtException e) {
            throw new AuthUnAuthorizedException(ErrorCode.UNAUTHORIZED_RT_EXPIRED);
        } catch (AuthRTException e) {
            throw new AuthUnAuthorizedException(ErrorCode.INTERNAL_RT_REDIS_ERROR);
        }
    }

    //로그아웃
    public void logout(final long userId, final String refreshTokenFromCookie) {
        try {
            checkIsSameRefreshToken(userId, refreshTokenFromCookie);
            refreshTokenManager.deleteRefreshToken(userId);
        } catch (AuthRTNotFoundException | AuthRTException e) {
            throw new AuthUnAuthorizedException(ErrorCode.UNAUTHORIZED_WRONG_RT);
        } catch (DataAccessException e) {
            throw new AuthRedisException(ErrorCode.INTERNAL_RT_REDIS_ERROR);
        }

    }

    private void checkIsSameRefreshToken(final long userId, final String refreshToken) {
        refreshTokenManager.validateSameWithOriginalRefreshToken(userId, refreshToken);
    }

    private String getUserSocialId(final SocialType socialType, final String socialAccessToken) {
        return loginStrategyManager.getStrategy(socialType).getUserSocialId(socialAccessToken);
    }

    private UserEntity createUser(final String userName,
                                  final int userAge,
                                  final Gender userGender,
                                  final String userEmail,
                                  final String userSocialId,
                                  final SocialType socialType
    ) {
        final UserEntity newUserEntity = UserEntity.create(
                userName,
                userGender,
                userAge,
                userEmail,
                userSocialId,
                socialType,
                UserRole.USER);
        return userSaver.saveUser(newUserEntity);
    }

    private UserSocialInfoDto getUserSocialInfo(final SocialType socialType,
                                                final String authorizationCode,
                                                final String redirectUrl) {
        return loginStrategyManager.getStrategy(socialType).getUserSocialInfo(authorizationCode, redirectUrl);
    }

    private User getUserBySocialInfo(final SocialType socialType, final String socialId) {
        return userRetriever.getUserBySocialInfo(socialType, socialId);
    }

    private void validDuplicatedUserBySocial(final SocialType socialType, final String socialId) {
        userRetriever.validDuplicatedUserBySocial(socialType, socialId);
    }

    private Token getSignUpJwtToken(final long userId) {
        return jwtProvider.issueToken(userId, UserRole.USER);
    }

    private Token getLoginOrReissueJwtToken(final long userId, final UserRole userRole) {
        return jwtProvider.issueToken(userId, userRole);
    }

    private void saveRefreshTokenInRedis(final long userId, final String refreshToken) {
        refreshTokenManager.saveRefreshTokenInRedis(userId, refreshToken, jwtProperties.refreshTokenExpirationTime());
    }
}
