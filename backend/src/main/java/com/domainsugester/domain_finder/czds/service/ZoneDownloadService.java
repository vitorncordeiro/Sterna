package com.domainsugester.domain_finder.czds.service;

import com.domainsugester.domain_finder.czds.client.AuthenticationClient;
import com.domainsugester.domain_finder.czds.client.ZoneFileDownloadClient;
import com.domainsugester.domain_finder.czds.dto.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZoneDownloadService {
    private final AuthenticationClient authenticationClient;
    private final ZoneFileDownloadClient zoneDownloadClient;
    @Value("${icann.account.username}")
    private String icannUsername;
    @Value("${icann.account.password}")
    private String icannPassword;
    private String bearerToken;

    public void downloadZoneFiles(){
        authenticate();

    }

    private void authenticate(){
        if(bearerToken == null || bearerToken.isEmpty()) {
            AuthRequest authRequest = new AuthRequest(icannUsername, icannPassword);
            bearerToken = authenticationClient.authenticate(authRequest).accessToken();
        }
    }
}
