package com.domainsugester.domain_finder.user.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    private String id;

    @Indexed(unique = true)
    private String googleId;

    private String name;
    private String email;
    private String pictureUrl;

    private Instant createdAt;
    private Instant lastLoginAt;
}
