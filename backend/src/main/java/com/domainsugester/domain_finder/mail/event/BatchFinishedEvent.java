package com.domainsugester.domain_finder.mail.event;

import java.util.List;
import java.util.Map;

public record BatchFinishedEvent(
        Integer batchSize,
        Map<String, String> domains
) {
}
