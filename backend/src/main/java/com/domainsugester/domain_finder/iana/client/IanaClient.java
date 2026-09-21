package com.domainsugester.domain_finder.iana.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="IanaClient", url="${iana.base-url}")
public interface IanaClient {
    @GetMapping("/domains/root/db/{tld}.html")
    String getTldInfoHtml(@PathVariable String tld);
}
