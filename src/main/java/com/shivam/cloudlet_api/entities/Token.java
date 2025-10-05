package com.shivam.cloudlet_api.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tokenId; // UUID recommended

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expireAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // Set expireAt automatically before persisting
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        expireAt = createdAt.plus(1, ChronoUnit.DAYS);
    }
}
