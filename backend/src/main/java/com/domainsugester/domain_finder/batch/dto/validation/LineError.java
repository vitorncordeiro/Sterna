package com.domainsugester.domain_finder.batch.dto.validation;

public record LineError(
        int line,
        String message
) {
}
