package com.permitseoul.permitserver.global;

import com.permitseoul.permitserver.global.exception.AlgorithmException;
import com.permitseoul.permitserver.global.exception.PermitGlobalException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class TicketCodeGenerator {
    private TicketCodeGenerator() {} // 인스턴스화 방지

    public static String generateTicketCode() {
        final String uuid = UUID.randomUUID().toString();

        try {
            final byte[] uuidBytes = uuid.getBytes(StandardCharsets.UTF_8);
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
