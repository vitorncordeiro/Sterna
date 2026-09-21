package com.domainsugester.domain_finder.tld.scheduler;

import com.domainsugester.domain_finder.tld.service.TldService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TldScheduler {
    private final TldService tldService;


    @Scheduled(fixedRate = 7 * 24 * 60 * 60 * 1000L)
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    public void refresh(){
        tldService.refreshBootstrap();
    }
}
