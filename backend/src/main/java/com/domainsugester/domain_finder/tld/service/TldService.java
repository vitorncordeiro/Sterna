package com.domainsugester.domain_finder.tld.service;

import com.domainsugester.domain_finder.registrar.hostinger.cache.HostingerTldCacheService;
import com.domainsugester.domain_finder.iana.cache.IanaBootstrapCacheService;
import com.domainsugester.domain_finder.registrar.hostinger.service.HostingerTldAvailabilityService;
import com.domainsugester.domain_finder.iana.service.IanaBootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TldService {
    private final HostingerTldAvailabilityService hostingerTldAvailabilityService;
    private final IanaBootstrapService ianaBootstrapService;
    private final HostingerTldCacheService hostingerTldCacheService;
    private final IanaBootstrapCacheService ianaTldCacheService;;

    public void refreshBootstrap(){
        Map<String, String> parsedIanaBootStrap = ianaBootstrapService.getIanaParsedBootstrap();
        parsedIanaBootStrap.forEach((tld, rdapUrl) -> {
            ianaTldCacheService.save(tld, rdapUrl);
        });

        Map<String, String> hostingerTlds = hostingerTldAvailabilityService.getAvailableTlds();
        hostingerTlds.forEach((tld, rdapUrl) -> {
            hostingerTldCacheService.save(tld, rdapUrl);
        });
    }


}
