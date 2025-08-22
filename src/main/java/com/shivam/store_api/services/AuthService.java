package com.shivam.store_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shivam.store_api.dto.LoginRequest;
import com.shivam.store_api.exceptions.CustomException;
import com.shivam.store_api.models.User;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    public User registerUser(User userData, HttpServletResponse response) {
        // Input validation
        if (userData.getUsername() == null || userData.getUsername().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        if (userData.getEmail() == null || userData.getEmail().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (userData.getPassword() == null || userData.getPassword().length() < 6) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters long");
        }

        try {
            // Check if user already exists
            userService.findByUsernameOrEmail(userData.getUsername(), userData.getEmail())
                    .ifPresent(existingUser -> {
                        throw new CustomException(HttpStatus.CONFLICT,
                                "User already exists with this username or email");
                    });

            // Hash password and save user
            String hashedPassword = passwordEncoder.encode(userData.getPassword());
            userData.setPassword(hashedPassword);

            User createdUser = userService.create(userData);

            // Set JWT tokens in cookies after successful registration
            jwtTokenService.setTokensInCookies(response, createdUser.getId());

            // Remove password from response
            createdUser.setPassword(null);

            return createdUser;

        } catch (CustomException e) {
            throw e; // Re-throw custom exceptions
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "User already exists with this username or email",
                    e);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to register user",
                    e);
        }
    }

    public User loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        // Input validation
        if (loginRequest.getUsernameOrEmail() == null || loginRequest.getUsernameOrEmail().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username or email is required");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        try {
            // Find user
            User user = userService.findByUsernameOrEmail(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getUsernameOrEmail())
                    .orElseThrow(() -> new CustomException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid username/email or password"));

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid username/email or password");
            }

            // Set JWT tokens in cookies
            jwtTokenService.setTokensInCookies(response, user.getId());

            // Remove password from response
            user.setPassword(null);

            return user;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Login failed",
                    e);
        }
    }

    public void logoutUser(HttpServletResponse response) {
        try {
            jwtTokenService.clearTokenCookies(response);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Logout failed",
                    e);
        }
    }
}
