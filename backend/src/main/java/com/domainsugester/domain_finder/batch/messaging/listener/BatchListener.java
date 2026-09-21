package com.domainsugester.domain_finder.batch.messaging.listener;

import com.domainsugester.domain_finder.shared.config.RabbitMQConfig;
import com.domainsugester.domain_finder.batch.messaging.events.DomainSubmitedEvent;
import com.domainsugester.domain_finder.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BatchListener {
    private final BatchService batchService;

    @RabbitListener(queues= RabbitMQConfig.DOMAIN_QUEUE)
    public void listen(DomainSubmitedEvent event) throws IOException {
        batchService.processDomain(event);
    }
}
