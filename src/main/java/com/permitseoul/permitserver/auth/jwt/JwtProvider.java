package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.domain.Token;
import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.user.domain.UserRole;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final JwtGenerator jwtGenerator;

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
    public long extractUserIdFromSubject(final String token) {
        final String subject = parseToken(token)
                .getBody()
                .getSubject();

        //subject가 숫자 문자열이어야 정상 변환됨
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new AuthWrongJwtException();
        }
    }

    //토큰 파싱
    private Jws<Claims> parseToken(final String token) {
        try {
            final JwtParser jwtParser = getJwtParser();
            return jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) { ///만료된 jwt 예외처리
            throw new AuthExpiredJwtException();
        } catch (JwtException | IllegalArgumentException e) { ///잘못된 jwt 예외처리
            throw new AuthWrongJwtException();
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtGenerator.getSecretKey())
                .build();
    }
}