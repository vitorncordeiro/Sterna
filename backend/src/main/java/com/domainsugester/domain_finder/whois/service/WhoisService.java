package com.domainsugester.domain_finder.whois.service;

import com.domainsugester.domain_finder.iana.client.IanaClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.whois.WhoisClient;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class WhoisService {
    private final IanaClient ianaClient;
    private static final List<String> REGISTERED_MARKERS = List.of(
            "domain name:",
            "creation date:",
            "registry expiry date:",
            "registrar:",
            "name server:",
            "nameservers:"
    );

    public String getWhoisServerUrl(String tld){
        String html = ianaClient.getTldInfoHtml(tld);
        String whoisServerUrl = extractWhoisServerUrl(html);
        return whoisServerUrl;
    }

    public String getWhoisResponse(String targetDomain, String whoisServer) throws IOException {
        WhoisClient whois = new WhoisClient();
        whois.connect(whoisServer);
        String response = whois.query(targetDomain).toLowerCase();
        whois.disconnect();
        int score = 0;

        for (String marker : REGISTERED_MARKERS) {
            if (response.contains(marker)) {
                score++;
            }
        }

        return score >= 2 ? "Registered" : "Available";

    }

    private String extractWhoisServerUrl(String html){
        Pattern pattern = Pattern.compile("whois\\.[^\\s<]+");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String whoisServer = matcher.group();
            return whoisServer;
        }
        return "unavailable";
    }
}
