package com.domainsugester.domain_finder.iana.controller;
import com.domainsugester.domain_finder.tld.dto.TldCachedResponse;
import com.domainsugester.domain_finder.iana.service.IanaBootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ianaBootstrap")
@RequiredArgsConstructor
public class IanaBootstrapController {
    private final IanaBootstrapService bootstrapService;

    @GetMapping
    public ResponseEntity<TldCachedResponse> getCachedBootstrap(){
        var response = bootstrapService.getCachedBootstrap();
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshIanaBootStrap(){
        bootstrapService.getIanaParsedBootstrap();
        return ResponseEntity.accepted().build();
    }
}
