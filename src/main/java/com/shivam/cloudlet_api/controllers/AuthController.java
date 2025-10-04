package com.shivam.cloudlet_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivam.cloudlet_api.dto.LoginRequest;
import com.shivam.cloudlet_api.dto.ResetPasswordRequest;
import com.shivam.cloudlet_api.dto.Response;
import com.shivam.cloudlet_api.dto.VerifyResetPasswordTokenRequest;
import com.shivam.cloudlet_api.models.User;
import com.shivam.cloudlet_api.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody User userData, HttpServletResponse response) {
        User createdUser = authService.registerUser(userData, response);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "User registered successfully!", createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        User user = authService.loginUser(loginRequest, response);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Login successful!", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response) {
        authService.logoutUser(response);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Logout successful!", null));
    }

    @GetMapping("/send-reset-password-email")
    public ResponseEntity<Response> getResetPasswordMail(@RequestParam String usernameOrEmail) {
        authService.getResetPasswordMail(usernameOrEmail);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Reset password mail sent successfully!", null));
    }

    @PostMapping("/verify-reset-password-token")
    public ResponseEntity<Response> verifyResetPasswordToken(@RequestBody VerifyResetPasswordTokenRequest requestBody) {
        authService.verifyToken(requestBody.getUserId(), requestBody.getToken());
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Token valid!", null));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordRequest requestBody) {
        authService.resetPassword(requestBody);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Password reset successfully!", null));
    }

}