package com.domainsugester.domain_finder.batch.dto.validation;

import java.util.Set;

public record TextValidationResult(
        Set<String> validDomains
) {}
