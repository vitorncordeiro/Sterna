package com.domainsugester.domain_finder.domain.dto;

import java.time.Instant;
import java.util.List;

public record DomainRequest(
        String domain,
        Boolean avaliable,
        Boolean registered,
        String registrar,
        Instant createDate,
        Instant expiryDate,
        List<String> nameServers
) {
}
