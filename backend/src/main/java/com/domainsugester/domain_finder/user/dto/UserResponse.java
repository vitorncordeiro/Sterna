package com.domainsugester.domain_finder.user.dto;

public record UserResponse(
        String id,
        String name,
        String email,
        String pictureUrl
    ) {}
