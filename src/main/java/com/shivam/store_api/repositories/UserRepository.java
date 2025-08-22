package com.shivam.store_api.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shivam.store_api.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameOrEmail(String username, String email);
}
