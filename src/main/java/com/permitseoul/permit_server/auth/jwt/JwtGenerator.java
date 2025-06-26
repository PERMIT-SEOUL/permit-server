package com.permitseoul.permit_server.auth.jwt;

import com.permitseoul.permit_server.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permit_server.auth.exception.AuthWrongJwtException;
import com.permitseoul.permit_server.global.Constants;
import com.permitseoul.permit_server.user.domain.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.coyote.BadRequestException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@EnableConfigurationProperties(JwtProperties.class)
@Component
public class JwtGenerator {

    private static final String USER_ROLE = "userRole";

    private final JwtProperties jwtProperties;
    private final Key secretKey;

    public JwtGenerator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    //AT 생성
    public String generateAccessToken(final long userId, final UserRole userRole) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, TokenType.ACCESS_TOKEN);

        final Claims claims = Jwts.claims();
        claims.put(USER_ROLE, userRole);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //RT 생성 후 캐싱
    @CachePut(value = Constants.REFRESH_TOKEN, key = "#userId") ///없으면 추가하고, 이미 있으면 업데이트
    public String generateRefreshToken(final long userId, final UserRole userRole) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, TokenType.REFRESH_TOKEN);

        final Claims claims = Jwts.claims();
        claims.put(USER_ROLE, userRole);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰 만료기간 생성
    private Date generateExpirationDate(final Date now, final TokenType tokenType) {
        return switch (tokenType) {
            case ACCESS_TOKEN -> new Date(now.getTime() + jwtProperties.accessTokenExpirationTime());
            case REFRESH_TOKEN -> new Date(now.getTime() + jwtProperties.refreshTokenExpirationTime());
        };
    }

    //토큰 파싱
    public Jws<Claims> parseToken(final String token) {
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
                .setSigningKey(secretKey)
                .build();
    }
}