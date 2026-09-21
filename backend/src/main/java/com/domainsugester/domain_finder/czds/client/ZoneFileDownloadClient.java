package com.domainsugester.domain_finder.czds.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ZoneDownloadClient", url = "${icann.authentication.base-url}")
public interface ZoneFileDownloadClient {

}
