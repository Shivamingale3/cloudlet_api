package com.shivam.cloudlet_api.entities;

import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.shivam.cloudlet_api.enums.FileUploadStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "owner_id", "logical_path" })
})
public class FileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String fileId;

  @Column(nullable = false)
  private String originalName; // e.g., "resume.pdf"

  @Column(nullable = false, unique = true)
  private String storageName; // e.g., "uuid__resume.pdf"

  @Column(nullable = false)
  private String logicalPath; // e.g., "/documents/personal/resume.pdf"

  @Column(nullable = false)
  private String physicalPath; // e.g., "/data/<userId>/allfiles/uuid__resume.pdf"

  private String mimeType;
  private Long sizeInBytes;
  private String checksum; // MD5 or SHA256

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private FileUploadStatus uploadStatus = FileUploadStatus.COMPLETED;

  @Builder.Default
  private Boolean isDeleted = false;

  @Builder.Default
  private Integer version = 1;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

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
