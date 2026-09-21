package com.domainsugester.domain_finder.domain.dto;

import java.time.Instant;
import java.util.Map;

public record DomainResponse(
        Map<String, Boolean> availability
) {
}
