package com.domainsugester.domain_finder.iana.client;

import com.domainsugester.domain_finder.iana.dto.IanaBootstrapResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="IanaBootstrapClient", url="${IANA_DATA_BASE_URL}")
public interface IanaBootstrapClient {
    @GetMapping("/rdap/dns.json")
    IanaBootstrapResponse getIanaBootstrap();
}
