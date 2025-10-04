package com.shivam.cloudlet_api.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.exceptions.CustomException;
import com.shivam.cloudlet_api.models.Token;
import com.shivam.cloudlet_api.repositories.TokenRepository;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Value("${frontend.url}")
    private String frontendUrl;

    private final SecureRandom random = new SecureRandom();

    public String generateRandomTokenString(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Token getByUserIdAndToken(String userId, String token) {
        try {
            return tokenRepository.findByUserIdAndToken(userId, token).orElseThrow(() -> {
                throw new CustomException(HttpStatus.NOT_FOUND, "Invalid or expired link. Request email again!");
            });
        } catch (CustomException e) {
            throw e;
        }
    }

    public Token createToken(String userId) {
        this.checkTokenLimit(userId);
        Token token = new Token();
        token.setUserId(userId);
        token.setToken(this.generateRandomTokenString(32));
        Date now = new Date();
        token.setCreatedAt(now);
        token.setExpireAt(new Date(now.getTime() + 15 * 60 * 1000));
        return tokenRepository.save(token);
    }

    public void deleteTokenByUserIdAndToken(String userId, String token) {
        tokenRepository.deleteByUserIdAndToken(userId, token);
    }

    public void delete(String tokenId) {
        tokenRepository.deleteById(tokenId);
    }

    public String generateTokenUrl(String userId, String token) {
        return frontendUrl + "/auth/reset-password?userId=" + userId + "&token=" + token;
    }

    public void verifyToken(String userId, String token) {
        Token tokenObj = this.getByUserIdAndToken(userId, token);
        if (tokenObj.getExpireAt().before(new Date())) {
            this.delete(tokenObj.getId());
            throw new CustomException(HttpStatus.BAD_REQUEST, "Link expired. Request email again!");
        }
    }

    public void deleteByUserId(String userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public void checkTokenLimit(String userId) {
        List<Token> tokens = tokenRepository.findByUserId(userId);

        // filter only non-expired tokens
        long activeTokens = tokens.stream()
                .filter(token -> token.getExpireAt().after(new Date()))
                .count();

        if (activeTokens >= 3) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Too many requests. Please try again later.");
        }
    }

}
