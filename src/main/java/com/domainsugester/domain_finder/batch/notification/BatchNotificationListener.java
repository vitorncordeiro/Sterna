package com.domainsugester.domain_finder.batch.notification;

import com.domainsugester.domain_finder.batch.messaging.events.FinishedBatchEvent;
import com.domainsugester.domain_finder.batch.pdf.PdfService;
import com.domainsugester.domain_finder.mail.service.SmtpEmailSender;
import com.domainsugester.domain_finder.shared.config.RabbitMQConfig;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BatchNotificationListener {
    private final PdfService pdfService;
    private final SmtpEmailSender smtpEmailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void listen(FinishedBatchEvent event){
        Map<String, Object> model = Map.of("domainAvailability", event.domainAvailability());
        byte[] pdf = pdfService.generatePdfFromTemplate("emails/batch_result", model);
        try{
            String recipient = event.recipientEmail();
            smtpEmailSender.sendWithAttachment(recipient, "Batch process finished", pdf, "batch-result.pdf");
        } catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
}