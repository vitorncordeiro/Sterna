package com.domainsugester.domain_finder.domain.service;

import com.domainsugester.domain_finder.registrar.hostinger.cache.HostingerTldCacheService;
import com.domainsugester.domain_finder.client.RdapClient;

import com.domainsugester.domain_finder.whois.service.WhoisService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class DomainService {
    private final RdapClient rdapClient;
    private final HostingerTldCacheService hostingerTldCacheService;
    private final WhoisService whoisService;
    public static final String AVAILABLE_DOMIN = "Available domain";

    public String getDomain(String domain) throws IOException {
        System.out.println(domain);
        String serverUrl = getRdapUrl(domain);
        if (serverUrl.contains("whois")){
            return whoisService.getWhoisResponse(domain, serverUrl);
        }else{
            return getRdapResponse(domain, serverUrl);
        }
    }
    private String getRdapResponse(String targetDomain, String rdapUrl){
        URI uri = URI.create(rdapUrl);

        try {
            Object domainInfo = rdapClient.getDomainInfo(uri, targetDomain);
            System.out.println(domainInfo);
            return domainInfo.toString();
        }catch (FeignException e){
            return AVAILABLE_DOMIN;
        }
    }
    private String getRdapUrl(String domain) {
        String[] parts = domain.split("\\.");
        String tld;
        if(parts.length > 2) {
            tld = parts[parts.length - 2] + "." + parts[parts.length - 1];
        }else{
            tld = parts[parts.length - 1];
        }
        return hostingerTldCacheService.fetchRdapUrl(tld);
    }
}
