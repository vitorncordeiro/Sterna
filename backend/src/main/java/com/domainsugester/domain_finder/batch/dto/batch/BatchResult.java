package com.domainsugester.domain_finder.batch.dto.batch;

import java.util.Map;

public record BatchResult(
        Map<String, Boolean> domainAvailability
) {
}
