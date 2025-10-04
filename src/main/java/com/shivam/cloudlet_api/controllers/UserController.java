package com.shivam.cloudlet_api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivam.cloudlet_api.dto.Response;
import com.shivam.cloudlet_api.models.User;

@RestController
@RequestMapping("api/users")
public class UserController {

    @GetMapping("/verify")
    public ResponseEntity<Response> authUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new Response(HttpStatus.OK, "User authenticated!", user));
    }

    @PostMapping("/create")
    public String create(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

}
