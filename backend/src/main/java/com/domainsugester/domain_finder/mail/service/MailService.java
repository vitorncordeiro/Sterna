package com.domainsugester.domain_finder.mail.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
    private final TemplateEngine templateEngine;
    private final SmtpEmailSender smtpEmailSender;

    public void sendWelcomeEmail(UUID eventId, String name, String email) throws MessagingException {
        String html = generateHtml(name);
        smtpEmailSender.send(email, html);
    }

    private String generateHtml(String name){
        Context context = new Context();
        context.setVariable("username", name);
        String html = templateEngine.process(
                "emails/welcome",
                context
        );
        return html;
    }
}
