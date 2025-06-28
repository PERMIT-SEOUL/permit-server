package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.auth.exception.AuthCookieException;
import com.permitseoul.permitserver.auth.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.auth.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.auth.jwt.CookieExtractor;
import com.permitseoul.permitserver.auth.jwt.JwtProvider;
import com.permitseoul.permitserver.global.exception.PermitUnAuthorizedException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final List<String> whiteURIList;

    private static final String ROLE = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            setAuthentication(request);
        } catch (AuthCookieException e) {
            if(!isWhiteListUrl(request.getRequestURI())) {
                throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_COOKIE);
            }
        } catch (AuthExpiredJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED_AT_EXPIRED);
        } catch (AuthWrongJwtException e) {
            throw new PermitUnAuthorizedException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            throw new PermitUnAuthorizedException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(final HttpServletRequest request) {
        final String token = CookieExtractor.getTokenCookie(request).getValue();
        final long userId = jwtProvider.extractUserIdFromToken(token);
        final String userRole = jwtProvider.extractUserRoleFromToken(token);
        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userRole));
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userId, null, authorities));
    }

    private boolean isWhiteListUrl(final String requestURI) {
        return whiteURIList.stream().anyMatch(requestURI::equals);
    }
}

