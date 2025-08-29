package com.permitseoul.permitserver.global.filter;

import com.permitseoul.permitserver.domain.auth.core.exception.AuthCookieException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthExpiredJwtException;
import com.permitseoul.permitserver.domain.auth.core.exception.AuthWrongJwtException;
import com.permitseoul.permitserver.domain.auth.core.jwt.CookieExtractor;
import com.permitseoul.permitserver.domain.auth.core.jwt.JwtProvider;
import com.permitseoul.permitserver.global.domain.CookieType;
import com.permitseoul.permitserver.global.exception.FilterException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final List<String> whiteURIList;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            setAuthentication(request);
            MDC.put("traceId", java.util.UUID.randomUUID().toString());
            filterChain.doFilter(request, response);
        } catch (AuthCookieException e) {
            if(!isWhiteListUrl(request.getRequestURI())) {
                throw new FilterException(ErrorCode.NOT_FOUND_AT_COOKIE);
            }
        } catch (AuthExpiredJwtException e) {
            throw new FilterException(ErrorCode.UNAUTHORIZED_AT_EXPIRED);
        } catch (AuthWrongJwtException e) {
            throw new FilterException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            throw new FilterException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.clear();
        }
    }

    private void setAuthentication(final HttpServletRequest request) {
        final String token = CookieExtractor.extractCookie(request, CookieType.ACCESS_TOKEN).getValue(); ///accessToken 쿠키 뽑음
        final long userId = jwtProvider.extractUserIdFromToken(token);
        final String userRole = jwtProvider.extractUserRoleFromToken(token);
        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userRole));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, authorities));
    }

    private boolean isWhiteListUrl(final String requestURI) {
        return whiteURIList.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}

