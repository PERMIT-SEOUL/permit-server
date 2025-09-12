package com.permitseoul.permitserver.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.exception.FilterException;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter { //필터 내부 전체 예외
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (FilterException e) {
            handleUnauthorizedException(response, e);
        }
        catch (Exception e) {
            handleException(response);
        }
    }

    private void handleUnauthorizedException(final HttpServletResponse response, final FilterException e) throws IOException {
        final ErrorCode errorCode = e.getErrorCode();
        final HttpStatus httpStatus = errorCode.getHttpStatus();
        setResponse(response, httpStatus, errorCode);
    }

    private void handleException(final HttpServletResponse response) throws IOException {
        setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private void setResponse(final HttpServletResponse response, final HttpStatus httpStatus, final ErrorCode errorCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(Constants.CHARACTER_TYPE);
        response.setStatus(httpStatus.value());
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(BaseResponse.of(errorCode)));
    }
}
