package com.shivam.store_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.shivam.store_api.models.Token;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByUserIdAndToken(String userId, String token);

    void deleteByUserIdAndToken(String userId, String token);

    void deleteById(@NonNull String tokenId);

    void deleteByUserId(@NonNull String userId);

    List<Token> findByUserId(String userId);
}
