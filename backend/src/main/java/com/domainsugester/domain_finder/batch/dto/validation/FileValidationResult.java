package com.domainsugester.domain_finder.batch.dto.validation;

import java.util.List;

public record FileValidationResult(
        List<String> validDomains, List<LineError> errors
) {}
