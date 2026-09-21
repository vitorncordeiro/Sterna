package com.domainsugester.domain_finder.domain.service;

import com.domainsugester.domain_finder.ai.service.NameVariationGeneratorService;
import com.domainsugester.domain_finder.client.RdapClient;
import com.domainsugester.domain_finder.domain.dto.DomainResponse;
import com.domainsugester.domain_finder.registrar.hostinger.cache.HostingerTldCacheService;
import com.domainsugester.domain_finder.whois.service.WhoisService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DomainService {

    public static final String AVAILABLE_DOMIN = "Available domain";
    private static final String WHOIS_AVAILABLE = "Available";

    private final RdapClient rdapClient;
    private final HostingerTldCacheService hostingerTldCacheService;
    private final WhoisService whoisService;
    private final NameVariationGeneratorService nameVariationGeneratorService;

    public DomainResponse getDomain(String domain, Boolean withAiAlternatives) throws IOException {
        if (isDomainAvailable(domain)) {
            return new DomainResponse(new HashMap<>(Map.of(domain, true)));
        }

        Map<String, Boolean> availabilityMap = new HashMap<>();
        availabilityMap.put(domain, false);

        if (Boolean.TRUE.equals(withAiAlternatives)) {
            List<String> variations = nameVariationGeneratorService.generateNameVariations(domain);

            for (String variation : variations) {
                try {
                    DomainResponse variationResponse = getDomain(variation, false);
                    if (variationResponse != null && variationResponse.availability() != null) {
                        availabilityMap.putAll(variationResponse.availability());
                    }
                } catch (Exception e) {
                    availabilityMap.put(variation, false);
                }
            }
        }

        return new DomainResponse(availabilityMap);
    }

    private boolean isDomainAvailable(String domain) throws IOException {
        String serverUrl = getRdapUrl(domain);

        if (serverUrl == null) {
            return false;
        }

        if (serverUrl.contains("whois")) {
            String whoisResponse = whoisService.getWhoisResponse(domain, serverUrl);
            return WHOIS_AVAILABLE.equalsIgnoreCase(whoisResponse);
        }

        return AVAILABLE_DOMIN.equalsIgnoreCase(getRdapResponse(domain, serverUrl));
    }

    private String getRdapResponse(String targetDomain, String rdapUrl) {
        URI uri = URI.create(rdapUrl);

        try {
            Object domainInfo = rdapClient.getDomainInfo(uri, targetDomain);
            return String.valueOf(domainInfo);
        } catch (FeignException e) {
            return AVAILABLE_DOMIN;
        }
    }

    private String getRdapUrl(String domain) {
        String[] parts = domain.split("\\.");
        String tld;

        if (parts.length > 2) {
            tld = parts[parts.length - 2] + "." + parts[parts.length - 1];
        } else {
            tld = parts[parts.length - 1];
        }

        return hostingerTldCacheService.fetchRdapUrl(tld);
    }
}