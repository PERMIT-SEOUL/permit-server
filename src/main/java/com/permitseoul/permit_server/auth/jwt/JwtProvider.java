package com.permitseoul.permit_server.auth.jwt;

import com.permitseoul.permit_server.auth.domain.Token;
import com.permitseoul.permit_server.auth.exception.AuthWrongJwtException;
import com.permitseoul.permit_server.user.domain.UserRole;
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
        final String subject = jwtGenerator.parseToken(token)
                .getBody()
                .getSubject();

        //subject가 숫자 문자열이어야 정상 변환됨
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new AuthWrongJwtException();
        }
    }
}