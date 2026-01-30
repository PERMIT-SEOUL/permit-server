package com.permitseoul.permitserver.domain.admin.util;

import com.permitseoul.permitserver.domain.admin.property.EmailProperties;
import com.permitseoul.permitserver.domain.admin.util.exception.EmailSendException;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component("guestTicketEmailSender")
@RequiredArgsConstructor
public class GuestTicketEmailSender {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;

    private final static String SENDER_NAME = "Ticket";
    private final static String CONTEXT_GUEST_NAME = "guestName";
    private final static String CONTEXT_EVENT_NAME = "eventName";
    private final static String CONTEXT_EVENT_TYPE = "eventType";
    private final static String CONTEXT_TICKET_CODES = "ticketCodes";
    private final static String TEMPLATE_NAME = "guest-ticket";
    private final static String INLINE_CONTENT_ID = "qr-";
    private final static String INLINE_CONTENT_TYPE = "image/png";

    public void sendGuestTicketsEmail(
            final String toEmail,
            final String guestName,
            final String eventName,
            final EventType eventType,
            final List<String> ticketCodes,
            final List<byte[]> qrPngs
    ) {
        try {
            final Context context = new Context();
            context.setVariable(CONTEXT_GUEST_NAME, guestName);
            context.setVariable(CONTEXT_EVENT_NAME, eventName);
            context.setVariable(CONTEXT_EVENT_TYPE, eventType.getDisplayName());
            context.setVariable(CONTEXT_TICKET_CODES, ticketCodes);
            final String html = templateEngine.process(TEMPLATE_NAME, context);

            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name()); //multipart:true는 인라인 이미지 추가
            helper.setFrom(emailProperties.sender(), SENDER_NAME);
            helper.setTo(toEmail);
            helper.setSubject("​[" + eventType.getDisplayName() + "] Guest Ticket Info");
            helper.setText(html, true);

            for (int i = 0; i < qrPngs.size(); i++) {
                final InputStreamSource src = new ByteArrayResource(qrPngs.get(i));
                helper.addInline(INLINE_CONTENT_ID + i, src, INLINE_CONTENT_TYPE);
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendException(ErrorCode.INTERNAL_EMAIL_SEND_ERROR);
        }
    }
}