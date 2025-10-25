package com.shivam.cloudlet_api.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "folders", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "name", "bucket_id", "parent_id" })
})
public class Folder {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String folderId;

  @Column(nullable = false)
  private String name;

  // Optional parent folder (null means it's at the root of the bucket)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Folder parent;

  // The user who created or owns this folder
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  // Audit-friendly metadata
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by")
  private User lastModifiedBy;

  @Builder.Default
  private Long sizeInBytes = 0L; // total size of all files under this folder

  @Builder.Default
  private Boolean shared = false; // optional sharing flag for later ACLs

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;
}
