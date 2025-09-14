package com.permitseoul.permitserver.domain.auth.core.jwt;

import com.permitseoul.permitserver.domain.auth.core.domain.TokenType;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
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

    public String generateAccessToken(final long userId, final UserRole userRole) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, TokenType.ACCESS_TOKEN);

        final Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(userId));
        claims.put(Constants.USER_ROLE, userRole.name());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(final long userId, final UserRole userRole) {
        final Date now = new Date();
        final Date expireDate = generateExpirationDate(now, TokenType.REFRESH_TOKEN);

        final Claims claims = Jwts.claims();
        claims.setSubject(String.valueOf(userId));
        claims.put(Constants.USER_ROLE, userRole);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date generateExpirationDate(final Date now, final TokenType tokenType) {
        return switch (tokenType) {
            case ACCESS_TOKEN -> new Date(now.getTime() + jwtProperties.accessTokenExpirationTime());
            case REFRESH_TOKEN -> new Date(now.getTime() + jwtProperties.refreshTokenExpirationTime());
        };
    }
}