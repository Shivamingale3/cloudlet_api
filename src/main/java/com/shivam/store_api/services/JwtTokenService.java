package com.shivam.store_api.services;

import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.shivam.store_api.config.JwtProperties;
import com.shivam.store_api.exceptions.InvalidTokenException;
import com.shivam.store_api.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtTokenService {

    @Autowired
    private JwtProperties jwtProperties;

    @Value("${app.local-environment:false}")
    private boolean isLocalEnvironment;

    private Key getSigningKey() {
        return new SecretKeySpec(jwtProperties.getSecret().getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateAuthToken(String userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getAuthExpiration());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token has expired");
        } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
            throw new InvalidTokenException("Invalid token format");
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed");
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException("Unable to validate token expiration");
        }
    }

    public void setTokensInCookies(HttpServletResponse response, String userId) {
        String authToken = generateAuthToken(userId);
        String refreshToken = generateRefreshToken(userId);

        // Auth token cookie
        Cookie authCookie = new Cookie("authToken", authToken);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(!isLocalEnvironment);
        authCookie.setPath("/");
        authCookie.setMaxAge((int) (jwtProperties.getAuthExpiration() / 1000));
        response.addCookie(authCookie);

        // Refresh token cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(!isLocalEnvironment);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (jwtProperties.getRefreshExpiration() / 1000));
        response.addCookie(refreshCookie);
    }

    public void clearTokenCookies(HttpServletResponse response) {
        Cookie authCookie = new Cookie("authToken", "");
        authCookie.setHttpOnly(true);
        authCookie.setSecure(!isLocalEnvironment);
        authCookie.setPath("/");
        authCookie.setMaxAge(0);
        response.addCookie(authCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(!isLocalEnvironment);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }
}
