package com.domainsugester.domain_finder.batch.messaging.publisher;

import com.domainsugester.domain_finder.shared.config.RabbitMQConfig;
import com.domainsugester.domain_finder.batch.messaging.events.DomainSubmitedEvent;
import com.domainsugester.domain_finder.batch.messaging.events.FinishedBatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchPublisher {
    private final RabbitTemplate template;

    public void publish(DomainSubmitedEvent event){
        template.convertAndSend(RabbitMQConfig.DOMAIN_EXCHANGE, RabbitMQConfig.DOMAIN_BINDING_KEY, event);
    }
    public void publish(FinishedBatchEvent event){
        template.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_BATCH_FINISHED_BINDING_KEY, event);
    }

}
