package com.domainsugester.domain_finder.iana.dto;

import com.domainsugester.domain_finder.tld.dto.TldCachedResponse;

import java.util.Map;

public record IanaTldCachedResponse(
        String description,
        String version,
        Map<String, String> tlds
) implements TldCachedResponse {}
