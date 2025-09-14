package com.permitseoul.permitserver.global;

import com.permitseoul.permitserver.global.exception.AlgorithmException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TicketOrCouponCodeGenerator {
    private static final String SHA_256_ALGORITHM = "SHA-256";

    public static String generateCode() {
        final String uuid = UUID.randomUUID().toString();

        try {
            final byte[] uuidBytes = uuid.getBytes(StandardCharsets.UTF_8);
            final MessageDigest digest = MessageDigest.getInstance(SHA_256_ALGORITHM);
            final byte[] hashBytes = digest.digest(uuidBytes);
            final StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                hex.append(String.format("%02x", hashBytes[i]));
            }
            return hex.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            //todo: log
            throw new AlgorithmException();
        }
    }
}
