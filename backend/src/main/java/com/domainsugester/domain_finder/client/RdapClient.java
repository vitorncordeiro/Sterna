package com.domainsugester.domain_finder.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@FeignClient(name="RdapClient", url="${rdap.client.url}")
public interface RdapClient{
    @GetMapping("/domain/{domain}")
    Object getDomainInfo(URI uri, @PathVariable String domain);
}
