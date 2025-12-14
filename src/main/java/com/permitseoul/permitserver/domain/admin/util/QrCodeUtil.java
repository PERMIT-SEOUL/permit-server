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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QrCodeUtil {
    private static final int QR_SIZE = 256;
    private static final String PNG = "PNG";
    private static final String URL_PATH = "/entry/guest";


    public static byte[] generatePng(final String baseUrl, final String ticketCode) {
        try {
            final String content = baseUrl + URL_PATH + ticketCode;

            final Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);

            final QRCodeWriter writer = new QRCodeWriter();
            final BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            final BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, PNG, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (WriterException | java.io.IOException e) {
            throw new QrCodeException(ErrorCode.INTERNAL_QRCODE_ERROR);
        }
    }
}