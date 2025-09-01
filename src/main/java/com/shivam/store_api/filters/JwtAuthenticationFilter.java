package com.shivam.store_api.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shivam.store_api.exceptions.CustomException;
import com.shivam.store_api.models.User;
import com.shivam.store_api.repositories.UserRepository;
import com.shivam.store_api.services.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authToken = extractTokenFromCookie(request, "authToken");
            String refreshToken = extractTokenFromCookie(request, "refreshToken");

            // If no cookie, fallback to Authorization header
            if (authToken == null) {
                authToken = extractTokenFromHeader(request);
            }

            if (authToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId;

            try {
                userId = jwtTokenService.extractUserId(authToken);
            } catch (Exception e) {
                if (refreshToken == null) {
                    throw new CustomException(
                            HttpStatus.UNAUTHORIZED,
                            "Authentication failed: Invalid or expired token",
                            e);
                }

                try {
                    userId = jwtTokenService.extractUserId(refreshToken);
                    jwtTokenService.setTokensInCookies(response, userId);
                } catch (Exception ex) {
                    throw new CustomException(
                            HttpStatus.UNAUTHORIZED,
                            "Authentication failed: Refresh token expired",
                            ex);
                }
            }

            User user = userRepository.findById(userId).orElseThrow(() -> {
                throw new CustomException(
                        HttpStatus.NOT_FOUND,
                        "User not found");
            });

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            handleCustomException(response, e);
        } catch (Exception e) {
            handleGenericException(response, e);
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null)
            return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json");

        String errorJson = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                e.getStatus().value(),
                e.getStatus().getReasonPhrase(),
                e.getMessage(),
                java.time.LocalDateTime.now());

        response.getWriter().write(errorJson);
    }

    private void handleGenericException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json");

        String errorJson = String.format(
                "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Authentication failed\", \"timestamp\": \"%s\"}",
                java.time.LocalDateTime.now());

        response.getWriter().write(errorJson);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/") ||
                path.equals("/health") ||
                path.equals("/");
    }
}
