package com.domainsugester.domain_finder.domain.controller;

import com.domainsugester.domain_finder.domain.dto.DomainResponse;
import com.domainsugester.domain_finder.domain.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
public class DomainController {
    private final DomainService domainService;

    @PostMapping("/{domain}")
    public ResponseEntity<DomainResponse> getDomain(@PathVariable String domain, @RequestParam Boolean withAiSuggestions) throws IOException {
        DomainResponse response = domainService.getDomain(domain, withAiSuggestions);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
