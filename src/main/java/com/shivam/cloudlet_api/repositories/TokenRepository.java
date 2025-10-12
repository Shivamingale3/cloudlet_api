package com.shivam.cloudlet_api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shivam.cloudlet_api.entities.Token;

@Repository
@Transactional
public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByUserIdAndToken(String userId, String token);

    void deleteByUserIdAndToken(String userId, String token);

    void deleteById(@NonNull String tokenId);

    void deleteByUserId(@NonNull String userId);

    Optional<Token> findByToken(String token);

    List<Token> findByUserId(String userId);
}
