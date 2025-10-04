package com.shivam.cloudlet_api.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.exceptions.CustomException;
import com.shivam.cloudlet_api.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(User userData) {
        try {
            return userRepository.save(userData);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new CustomException(
                    HttpStatus.CONFLICT,
                    "User already exists with this username or email",
                    e);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create user",
                    e);
        }
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

    public void delete(String id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new CustomException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id);
            }
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
}