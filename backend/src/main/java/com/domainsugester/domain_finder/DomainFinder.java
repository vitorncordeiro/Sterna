package com.domainsugester.domain_finder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableRetry
@SpringBootApplication
@EnableFeignClients
public class DomainFinder {

	public static void main(String[] args) {
		SpringApplication.run(DomainFinder.class, args);
	}

}

