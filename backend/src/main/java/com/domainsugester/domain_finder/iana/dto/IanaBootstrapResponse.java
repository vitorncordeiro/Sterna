package com.domainsugester.domain_finder.iana.dto;

import java.time.Instant;
import java.util.List;

public record IanaBootstrapResponse(
        String description,
        Instant publication,
        List<List<List<String>>> services,
        String version
) {
}

