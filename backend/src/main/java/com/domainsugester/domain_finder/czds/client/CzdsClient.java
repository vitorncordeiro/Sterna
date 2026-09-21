package com.domainsugester.domain_finder.czds.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "CzdsClient", url = "${czds.base-url}")
public interface CzdsClient {
    @GetMapping("/czds/downloads/links")
    List<String> getDownloadLinks(@RequestHeader("Authorization") String bearerToken);
}
