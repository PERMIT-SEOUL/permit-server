package com.permitseoul.permitserver.auth.jwt;

import com.permitseoul.permitserver.auth.domain.TokenType;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.user.domain.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@EnableConfigurationProperties(JwtProperties.class)
@Component
public class JwtGenerator {

    private final JwtProperties jwtProperties;

    @Getter
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
        claims.put(Constants.USER_ROLE, userRole.name());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //RT 생성 후 캐싱
    @CachePut(value = Constants.REFRESH_TOKEN, key = "#p0") ///p0은 첫번째 파라미터를 의미함
    public String generateRefreshToken(final long userId, final UserRole userRole) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, TokenType.REFRESH_TOKEN);

        final Claims claims = Jwts.claims();
        claims.put(Constants.USER_ROLE, userRole);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(userId))
                .setClaims(claims)
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
}