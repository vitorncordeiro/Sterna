package com.domainsugester.domain_finder.iana.service;

import com.domainsugester.domain_finder.tld.cache.TldCacheService;
import com.domainsugester.domain_finder.iana.client.IanaBootstrapClient;
import com.domainsugester.domain_finder.iana.dto.IanaBootstrapResponse;
import com.domainsugester.domain_finder.tld.dto.TldCachedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IanaBootstrapService {
    private final TldCacheService ianaBootstrapCacheService;
    private final IanaBootstrapClient ianaBootstrapClient;
    private final Duration TTL = Duration.ofDays(10);

    public Map<String, String> getIanaParsedBootstrap(){

        IanaBootstrapResponse ianaResponse = ianaBootstrapClient.getIanaBootstrap();
        Map<String, String> parsedBootStrap = parseRawBootstrap(ianaResponse);

        return parsedBootStrap;
    }
    public TldCachedResponse getCachedBootstrap(){
        return ianaBootstrapCacheService.fetchBootstrap();
    }

    private Map<String, String> parseRawBootstrap(IanaBootstrapResponse bootstrap){
        Map<String, String> map = new HashMap<>();
        map.put("iana:description", bootstrap.description());
        map.put("iana:version", bootstrap.version());
        bootstrap.services().forEach(pair -> {
            String rdapUrl = pair.get(1).get(0);
            pair.get(0).forEach(tld -> map.put(("iana:tld:" + tld), rdapUrl));
        });
        return map;
    }
}
