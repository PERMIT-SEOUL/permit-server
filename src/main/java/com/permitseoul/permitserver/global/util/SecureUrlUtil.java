package com.permitseoul.permitserver.global.util;

import com.permitseoul.permitserver.global.HashIdProperties;
import com.permitseoul.permitserver.global.exception.UrlSecureException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;


@Component
public class SecureUrlUtil {
    private final Hashids hashids;

    public SecureUrlUtil (final HashIdProperties hashIdProperties) {
        this.hashids = new Hashids(hashIdProperties.salt(), hashIdProperties.length());
    }

    public String encode(final Long id) {
        if (id == null) {
            throw new UrlSecureException(ErrorCode.INTERNAL_ID_ENCODE_ERROR);
        }
        return hashids.encode(id);
    }

    public long decode(final String hash) {
        final long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new UrlSecureException(ErrorCode.BAD_REQUEST_ID_DECODE_ERROR);
        }
        return decoded[0];
    }
}
