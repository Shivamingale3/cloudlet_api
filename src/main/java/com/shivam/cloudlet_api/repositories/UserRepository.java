package com.shivam.cloudlet_api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shivam.cloudlet_api.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameOrEmail(String username, String email);
}
