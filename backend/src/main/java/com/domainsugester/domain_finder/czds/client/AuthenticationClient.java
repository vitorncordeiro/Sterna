package com.domainsugester.domain_finder.czds.client;

import com.domainsugester.domain_finder.czds.dto.AuthRequest;
import com.domainsugester.domain_finder.czds.dto.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CzdsAuthenticationClient", url = "${czds.base-url}")
public interface AuthenticationClient {
    @PostMapping("/api/authenticate/")
    AuthResponse authenticate(@RequestBody AuthRequest authRequest);
}
