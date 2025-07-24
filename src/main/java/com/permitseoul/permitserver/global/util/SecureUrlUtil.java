package com.permitseoul.permitserver.global.util;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;


@Component
public class SecureUrlUtil {
    private final Hashids hashids;

    public SecureUrlUtil (final HashIdProperties hashIdProperties) {
        this.hashids = new Hashids(hashIdProperties.salt(), hashIdProperties.length());
    }

    public String encode(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return hashids.encode(id);
    }

    public Long decode(String hash) {
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid Hash ID: " + hash);
        }
        return decoded[0];
    }
}
