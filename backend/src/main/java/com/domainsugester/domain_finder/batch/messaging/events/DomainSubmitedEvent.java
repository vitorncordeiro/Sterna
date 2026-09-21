package com.domainsugester.domain_finder.batch.messaging.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DomainSubmitedEvent(
        String domain,
        UUID batchId
) {
}
