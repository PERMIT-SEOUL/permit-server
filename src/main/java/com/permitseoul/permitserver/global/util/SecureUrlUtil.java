package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.exception.UrlDecodeException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;

import java.util.Base64;

public abstract class SecureUrlUtil {
    public static String encodeUrl(final Long id) {
        return Base64.getUrlEncoder().encodeToString(id.toString().getBytes());
    }

    public static Long decodeUrl(final String url) {
        try {
            return Long.parseLong(new String(Base64.getUrlDecoder().decode(url)));
        } catch (IllegalArgumentException e) {
            throw new UrlDecodeException(ErrorCode.BAD_REQUEST_ID_DECODE_ERROR);
        }
    }
}
