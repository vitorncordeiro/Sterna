package com.domainsugester.domain_finder.mail.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;

@Service
@RequiredArgsConstructor
public class SmtpEmailSender {
    private final JavaMailSender mailSender;
    @Value("${mail.from}")
    private String fromAddress;

    public void send(String email, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Olá!");
        helper.setText(body, true);
        mailSender.send(message);
    }

    public void sendWithAttachment(String email, String body, byte[] attachment, String filename) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Batch result");
        helper.setText(body, true);
        helper.addAttachment(filename, new ByteArrayResource(attachment));
        mailSender.send(message);
    }
}