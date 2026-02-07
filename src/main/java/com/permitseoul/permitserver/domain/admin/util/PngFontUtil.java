package com.permitseoul.permitserver.domain.admin.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class PngFontUtil {
    private static final Font BASE = loadBase();
    private static final Font SIZE_12 = BASE.deriveFont(Font.PLAIN, 12f);


    private static Font loadBase() {
        try (InputStream is = new ClassPathResource("fonts/NotoSansKR-Regular.ttf").getInputStream()) {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            log.warn("Failed to load font", e);
            return new Font("SansSerif", Font.PLAIN, 12);
        }
    }

    public static Font size12() {
        return SIZE_12;
    }
}
