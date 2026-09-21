package com.domainsugester.domain_finder.registrar.hostinger.service;

import com.domainsugester.domain_finder.whois.service.WhoisService;
import com.domainsugester.domain_finder.iana.cache.IanaBootstrapCacheService;
import com.domainsugester.domain_finder.tld.service.TldAvailabilityService;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostingerTldAvailabilityService implements TldAvailabilityService {

    private final WhoisService whoisService;
    @Value("${playwright.server.url:}")
    private String playwrightServerUrl;
    private final IanaBootstrapCacheService ianaBootstrapCacheService;

    public Map<String, String> getAvailableTlds() {
        try (Playwright playwright = Playwright.create(
                new Playwright.CreateOptions()
                        .setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1"))
        )) {
            Browser browser = playwright.chromium().connect(
                    playwrightServerUrl,
                    new BrowserType.ConnectOptions().setTimeout(30_000)
            );

            Page page = browser.newPage();
            Response response = page.waitForResponse(
                    r -> r.url().contains("available-tlds-by-theme"),
                    () -> page.navigate(
                            "https://www.hostinger.com/domain-name-results" +
                                    "?from=homepage&domain=dawda.com"
                    )
            );

            String result = response.text();
            log.info("TLDs Successfully catched. Status: {}", response.status());
            browser.close();
            return parseAvaliableTlds(result);

        } catch (Exception e) {
            throw new RuntimeException("Error while capturing TLDs", e);
        }
    }
    private Map<String, String> parseAvaliableTlds(String tlds){
        Map<String, String> map = new HashMap<>();
        tlds = tlds.substring(tlds.indexOf("[") + 1, tlds.lastIndexOf("]"));
        String[] tldArray = tlds.split(",");
        for (String tld : tldArray) {
            tld = tld.substring(1, tld.length() - 1);
            map.put("hostinger:tld:" + tld, getRdapUrl(tld));
        }

        return map;
    }
    @Override
    public String getRdapUrl(String tld){
        if(tld.contains(".")){
            tld = tld.split("\\.")[1];
        }
        String rdapUrl = ianaBootstrapCacheService.get("iana:tld:" + tld);

        if(rdapUrl == null){
            return whoisService.getWhoisServerUrl(tld);
        }
        return rdapUrl;
    }

}