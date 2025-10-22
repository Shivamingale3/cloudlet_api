package com.shivam.cloudlet_api.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.shivam.cloudlet_api.enums.BucketAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buckets", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "name", "owner_id" })
})
public class Bucket {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String bucketId;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BucketAccess access;

  @Builder.Default
  private Long storageUsed = 0L;
  private Long storageLimit;

  // Optional: who last modified the bucket (for audit linkage)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by")
  private User lastModifiedBy;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;
}
