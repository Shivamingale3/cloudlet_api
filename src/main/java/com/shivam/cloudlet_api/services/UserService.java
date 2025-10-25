package com.shivam.cloudlet_api.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.dto.EmailDetails;
import com.shivam.cloudlet_api.dto.users.request.CompleteProfileDto;
import com.shivam.cloudlet_api.dto.users.response.UserResponseDto;
import com.shivam.cloudlet_api.entities.ActivityLog;
import com.shivam.cloudlet_api.entities.Token;
import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.enums.ActivityType;
import com.shivam.cloudlet_api.enums.UserRole;
import com.shivam.cloudlet_api.enums.UserStatus;
import com.shivam.cloudlet_api.exceptions.CustomException;
import com.shivam.cloudlet_api.repositories.UserRepository;
import com.shivam.cloudlet_api.utilities.EmailTemplateUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private ActivityService activityLogService;

    public List<User> findAll() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve users",
                    e);
        }
    }

    public User create(User userData) {
        try {
            if (userRepository.existsByEmail(userData.getEmail())) {
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "User already exists with email: " + userData.getEmail());
            }
            return userRepository.save(userData);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create user",
                    e);
        }
    }

    public void inviteUser(String userId, String email) {
        try {
            if (tokenService.tokenExists(userId)) {
                tokenService.deleteByUserId(userId);
            }
            Token token = tokenService.createToken(userId);
            String link = tokenService.generateInvitationUrl(token.getToken());
            emailService.sendHtmlMail(EmailDetails.builder().recipient(email)
                    .msgBody(EmailTemplateUtil.buildAccountInvitationEmail(email, link))
                    .subject("Invitation | Cloudlet").build());
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to invite user",
                    e);
        }
    }

    public void checkUsername(String username) {
        if (userRepository.existsByUsernameAllIgnoringCase(username)) {
            throw new CustomException(HttpStatus.CONFLICT, "Username is already taken!");
        }
        return;
    }

    public Optional<User> findByUsernameOrEmail(String userName, String email) {
        try {
            return userRepository.findByUsernameOrEmail(userName, email);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to find user",
                    e);
        }
    }

    public User findById(String id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new CustomException(
                            HttpStatus.NOT_FOUND,
                            "User not found with id: " + id));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve user",
                    e);
        }
    }

    public User update(String id, User userData) {
        try {
            User existingUser = findById(id);

            if (userData.getUsername() != null) {
                existingUser.setUsername(userData.getUsername());
            }
            if (userData.getEmail() != null) {
                existingUser.setEmail(userData.getEmail());
            }
            if (userData.getRole() != null) {
                existingUser.setRole(userData.getRole());
            }
            return userRepository.save(existingUser);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update user",
                    e);
        }
    }

    public void updateUserRole(String userId, UserRole role, User requestingUser) {
        try {
            User existingUser = findById(userId);
            existingUser.setRole(role);
            userRepository.save(existingUser);
            logUsersActivity(existingUser, ActivityType.MODIFIED,
                    requestingUser.getUsername() + " changed " + existingUser.getUsername() + " role to " + role);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update user status",
                    e);
        }
    }

    public void updateUserStatus(UserStatus userStatus, String userId, User requestingUser) {
        try {
            User existingUser = findById(userId);
            existingUser.setStatus(userStatus);
            userRepository.save(existingUser);
            logUsersActivity(existingUser, ActivityType.MODIFIED,
                    requestingUser.getUsername() + userStatus + " user " + existingUser.getUsername());
            return;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update user status",
                    e);
        }
    }

    public void completeProfile(CompleteProfileDto data, String userId) {
        try {
            User existingUser = findById(userId);
            if (existingUser.getStatus() != UserStatus.INVITED) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Profile already completed!");
            }
            existingUser.setPassword(passwordEncoder.encode(data.getPassword()));
            existingUser.setAvatar(data.getAvatar());
            existingUser.setUsername(data.getUsername());
            existingUser.setStatus(UserStatus.ACTIVE);
            userRepository.save(existingUser);
            return;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to complete profile",
                    e);
        }
    }

    public void delete(String id, User requestingUser) {
        try {
            User existingUser = findById(id);
            if (existingUser == null) {
                throw new CustomException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id);
            }
            logUsersActivity(existingUser, ActivityType.DELETED,
                    requestingUser.getUsername() + " deleted user " + existingUser.getUsername());
            userRepository.deleteById(id);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete user",
                    e);
        }
    }

    public void updatePassword(String id, String password) {
        try {
            User existingUser = findById(id);
            existingUser.setPassword(passwordEncoder.encode(password));
            userRepository.save(existingUser);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update password",
                    e);
        }
    }

    public void logUsersActivity(User actor, ActivityType activityType, String log) {
        activityLogService.saveActivityLog(ActivityLog.builder()
                .actor(actor)
                .activityType(activityType)
                .log(log)
                .build());
    }

    public UserResponseDto mapUserToDto(User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(null) // Never expose password in response
                .role(user.getRole())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt() != null ? Date.from(user.getCreatedAt()) : null)
                .updatedAt(user.getUpdatedAt() != null ? Date.from(user.getUpdatedAt()) : null)
                .build();
    }
}