package com.shivam.cloudlet_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivam.cloudlet_api.dto.Response;
import com.shivam.cloudlet_api.dto.users.request.CreateUserDto;
import com.shivam.cloudlet_api.dto.users.request.UpdateRoleDto;
import com.shivam.cloudlet_api.dto.users.request.UpdateStatusDto;
import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.enums.UserRole;
import com.shivam.cloudlet_api.enums.UserStatus;
import com.shivam.cloudlet_api.exceptions.CustomException;
import com.shivam.cloudlet_api.services.TokenService;
import com.shivam.cloudlet_api.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("")
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(new Response(HttpStatus.OK, "Fetched all users!", userService.findAll()));
    }

    @PostMapping("")
    public ResponseEntity<Response> createUser(@RequestBody CreateUserDto userDetails) {
        User createdUser = userService.create(User.builder()
                .username(userDetails.getEmail())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .status(UserStatus.INVITED)
                .build());

        userService.inviteUser(createdUser.getUserId(), createdUser.getEmail());
        return ResponseEntity.ok()
                .body(new Response(HttpStatus.CREATED, "User created and invitation link set!", null));
    }

    @GetMapping("/re-invite/{userId}")
    public ResponseEntity<Response> reInviteUser(@PathVariable String userId) {
        User user = userService.findById(userId);
        if (tokenService.tokenExists(userId)) {
            tokenService.deleteByUserId(userId);
        }
        userService.inviteUser(user.getUserId(), user.getEmail());
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Invitation sent successfully!", null));
    }

    @GetMapping("/verify")
    public ResponseEntity<Response> authUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new Response(HttpStatus.OK, "User authenticated!", user));
    }

    @PutMapping("/status/{userId}")
    public ResponseEntity<Response> updateStatus(@PathVariable String userId, @RequestBody UpdateStatusDto data,
            @AuthenticationPrincipal User requestingUser) {
        if (!requestingUser.getRole().equals(UserRole.ADMIN)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Operation not allowed for this user!");
        }
        userService.updateUserStatus(data.getStatus(), userId);
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Status updated successfully!", null));
    }

    @PutMapping("/role/{userId}")
    public ResponseEntity<Response> updateRole(@PathVariable String userId, @RequestBody UpdateRoleDto role,
            @AuthenticationPrincipal User requestingUser) {
        if (!requestingUser.getRole().equals(UserRole.ADMIN)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Operation not allowed for this user!");
        }
        userService.updateUserRole(userId, role.getRole());
        return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Role updated successfully!", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable String userId) {
        userService.delete(userId);
        return ResponseEntity.ok(new Response(HttpStatus.OK, "User deleted!", null));
    }

}
