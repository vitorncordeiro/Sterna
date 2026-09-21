package com.domainsugester.domain_finder.tld.cache;

import com.domainsugester.domain_finder.tld.dto.TldCachedResponse;

public interface TldCacheService {

    void save(String key, String value);
    TldCachedResponse fetchBootstrap();
}