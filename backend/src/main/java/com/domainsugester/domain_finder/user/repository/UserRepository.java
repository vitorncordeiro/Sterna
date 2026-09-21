package com.domainsugester.domain_finder.user.repository;

import com.domainsugester.domain_finder.user.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByGoogleId(String googleId);
}
