package com.domainsugester.domain_finder.batch.messaging.events;

import java.util.Map;

public record FinishedBatchEvent(
        Map<String, Boolean> domainAvailability,
        String recipientEmail
) {
}
