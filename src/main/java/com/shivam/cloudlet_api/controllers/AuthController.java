package com.shivam.cloudlet_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivam.cloudlet_api.dto.LoginRequest;
import com.shivam.cloudlet_api.dto.ResetPasswordRequest;
import com.shivam.cloudlet_api.dto.Response;
import com.shivam.cloudlet_api.dto.VerifyResetPasswordTokenRequest;
import com.shivam.cloudlet_api.dto.users.request.CompleteProfileDto;
import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.services.AuthService;
import com.shivam.cloudlet_api.services.TokenService;
import com.shivam.cloudlet_api.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Response> checkUsername(@PathVariable String username) {
        userService.checkUsername(username);
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Username Available", null));
    }

    @GetMapping("/verify-invitation/{token}")
    public ResponseEntity<Response> verifyInvitation(@PathVariable String token) {
        String userId = this.tokenService.verifyInvitationTokenReturnUserId(token);
        User userData = this.userService.findById(userId);
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Token valid", userData));
    }

    @PatchMapping("/complete-profile")
    public ResponseEntity<Response> completeProfile(
            @RequestBody CompleteProfileDto profile) {
        String userId = tokenService.verifyInvitationTokenReturnUserId(profile.getToken());
        userService.completeProfile(profile, userId);
        tokenService.deleteByUserId(userId);
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Profile completed successfully!", null));
    }

}