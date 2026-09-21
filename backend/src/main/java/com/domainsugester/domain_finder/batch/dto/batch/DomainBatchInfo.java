package com.domainsugester.domain_finder.batch.dto.batch;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DomainBatchInfo(
        Integer batchSize,
        Integer batchRemaining,
        UUID batchId
) {
}
