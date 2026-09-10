package com.domainsugester.domain_finder.shared.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Domain
    public static final String DOMAIN_QUEUE = "domain.queue";
    public static final String DOWNLOADED_DOMAINS_QUEUE = "downloadedDomain.queue";
    public static final String DOMAIN_EXCHANGE = "domain.exchange";
    public static final String DOMAIN_BINDING_KEY = "domain.requested";
    public static final String DOWNLOADED_DOMAINS_BINDING_KEY = "downloadedDomain.requested";
    public static final String DOMAIN_DLQ = "domain.queue.dlq";
    public static final String DOMAIN_DLX = "domain.exchange.dlx";
    public static final String DOMAIN_DLQ_BINDING_KEY = "domain.requested.dlq";

    // Email
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String EMAIL_BINDING_KEY = "email.requested";
    public static final String EMAIL_BATCH_FINISHED_BINDING_KEY = "batch.finished";
    public static final String EMAIL_DLQ = "email.queue.dlq";
    public static final String EMAIL_DLX = "email.exchange.dlx";
    public static final String EMAIL_DLQ_BINDING_KEY = "email.requested.dlq";

    // ===== Domain =====

    @Bean
    public TopicExchange domainExchange() {
        return new TopicExchange(DOMAIN_EXCHANGE, true, false);
    }

    @Bean
    public Queue domainQueue() {
        return QueueBuilder.durable(DOMAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DOMAIN_DLX)
                .withArgument("x-dead-letter-routing-key", DOMAIN_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Queue downloadedDomainsQueue() {
        return QueueBuilder.durable(DOWNLOADED_DOMAINS_QUEUE)
                .withArgument("x-dead-letter-exchange", DOMAIN_DLX)
                .withArgument("x-dead-letter-routing-key", DOMAIN_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Binding domainBinding(Queue domainQueue, TopicExchange domainExchange) {
        return BindingBuilder.bind(domainQueue).to(domainExchange).with(DOMAIN_BINDING_KEY);
    }

    @Bean
    public Binding downloadedDomainsBinding(Queue downloadedDomainsQueue, TopicExchange domainExchange) {
        return BindingBuilder.bind(downloadedDomainsQueue).to(domainExchange).with(DOWNLOADED_DOMAINS_BINDING_KEY);
    }

    @Bean
    public Queue domainDeadLetterQueue() {
        return QueueBuilder.durable(DOMAIN_DLQ).build();
    }

    @Bean
    public DirectExchange domainDeadLetterExchange() {
        return new DirectExchange(DOMAIN_DLX, true, false);
    }

    @Bean
    public Binding domainDeadLetterBinding(Queue domainDeadLetterQueue, DirectExchange domainDeadLetterExchange) {
        return BindingBuilder.bind(domainDeadLetterQueue).to(domainDeadLetterExchange).with(DOMAIN_DLQ_BINDING_KEY);
    }

    // ===== Email =====

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", EMAIL_DLX)
                .withArgument("x-dead-letter-routing-key", EMAIL_DLQ_BINDING_KEY)
                .build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_BINDING_KEY);
    }

    @Bean
    public Binding emailBatchFinishedBinding(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_BATCH_FINISHED_BINDING_KEY);
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    @Bean
    public DirectExchange emailDeadLetterExchange() {
        return new DirectExchange(EMAIL_DLX, true, false);
    }

    @Bean
    public Binding emailDeadLetterBinding(Queue emailDeadLetterQueue, DirectExchange emailDeadLetterExchange) {
        return BindingBuilder.bind(emailDeadLetterQueue).to(emailDeadLetterExchange).with(EMAIL_DLQ_BINDING_KEY);
    }

    // ===== Shared =====

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}