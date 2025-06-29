package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.user.domain.UserRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final JwtGenerator jwtGenerator;
    private final CacheManager cacheManager;

    public Token issueToken(final long userId, final UserRole userRole) {
        return Token.of(
                generateAccessToken(userId, userRole),
                generateRefreshToken(userId, userRole)
        );
    }

    private String generateAccessToken(final long userId, final UserRole userRole) {
        return jwtGenerator.generateAccessToken(userId, userRole);
    }

    public String generateRefreshToken(final long userId, final UserRole userRole) {
        return jwtGenerator.generateRefreshToken(userId, userRole);
    }

    //jwtSubject에서 userId추출
    public long extractUserIdFromToken(final String token) {
        final String subject = parseToken(token)
                .getBody()
                .getSubject();
        if (subject == null) {
            throw new AuthWrongJwtException();
        }

        //subject가 숫자 문자열이어야 정상 변환됨
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new AuthWrongJwtException();
        }
    }

    public String extractUserRoleFromToken(final String token) {
        final Jws<Claims> claims = parseToken(token);
        return claims.getBody().get(Constants.USER_ROLE, String.class);
    }

    public String getRefreshTokenFromCache(final long userId) {
        return cacheManager.getCache(Constants.REFRESH_TOKEN)
                .get(userId, String.class);
    }

    //토큰 파싱
    private Jws<Claims> parseToken(final String token) {
        try {
            final JwtParser jwtParser = getJwtParser();
            return jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) { ///만료된 jwt 예외처리
            throw new AuthExpiredJwtException();
        } catch (Exception e) {
            throw new AuthWrongJwtException();
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtGenerator.getSecretKey())
                .build();
    }
}