package com.domainsugester.domain_finder.batch.dto.request;

import java.util.List;

public record BatchTextRequest(
        List<String> domains
) {
}
