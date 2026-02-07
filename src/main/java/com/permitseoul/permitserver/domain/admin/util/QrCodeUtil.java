package com.permitseoul.permitserver.domain.admin.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.permitseoul.permitserver.domain.admin.util.exception.QrCodeException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QrCodeUtil {

    private static final int QR_SIZE = 256;
    private static final String PNG = "PNG";
    private static final String URL_PATH = "/entry/guest/";

    private static final int TEXT_AREA_HEIGHT = 46; // 텍스트 영역 높이
    private static final int TEXT_PADDING_X = 12;

    /**
     * ✅ 이벤트명을 QR "위"에 표시 (텍스트 영역 추가)
     */
    public static byte[] generatePng(final String baseUrl, final String ticketCode, final String eventName) {
        try {
            final String content = baseUrl + URL_PATH + ticketCode;

            final Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);

            final QRCodeWriter writer = new QRCodeWriter();
            final BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            final BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

            final BufferedImage finalImage = (eventName == null || eventName.isBlank())
                    ? qrImage
                    : addEventNameOnTop(qrImage, "[ " + eventName + " ]");

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(finalImage, PNG, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();

        } catch (WriterException | java.io.IOException e) {
            throw new QrCodeException(ErrorCode.INTERNAL_QRCODE_ERROR);
        }
    }

    private static BufferedImage addEventNameOnTop(final BufferedImage qrImage, final String label) {
        final int width = qrImage.getWidth();
        final int height = qrImage.getHeight();

        final BufferedImage combined = new BufferedImage(
                width,
                height + TEXT_AREA_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        final Graphics2D g = combined.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, combined.getWidth(), combined.getHeight());

            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.drawImage(qrImage, 0, TEXT_AREA_HEIGHT, null);

            g.setColor(Color.BLACK);
            g.setFont(PngFontUtil.size12());
            final FontMetrics fm = g.getFontMetrics();

            final String text = ellipsize(label, fm, width - (TEXT_PADDING_X * 2));
            final int textWidth = fm.stringWidth(text);
            final int x = (width - textWidth) / 2;
            final int y = ((TEXT_AREA_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(text, x, y);

            return combined;
        } finally {
            g.dispose();
        }
    }

    private static String ellipsize(final String text, final FontMetrics fm, final int maxWidth) {
        if (fm.stringWidth(text) <= maxWidth) return text;

        final String ellipsis = "...";
        int len = text.length();

        while (len > 0) {
            final String candidate = text.substring(0, len) + ellipsis;
            if (fm.stringWidth(candidate) <= maxWidth) return candidate;
            len--;
        }
        return ellipsis;
    }
}
