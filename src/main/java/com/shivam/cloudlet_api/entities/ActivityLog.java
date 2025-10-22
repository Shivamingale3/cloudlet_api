package com.shivam.cloudlet_api.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.shivam.cloudlet_api.enums.ActivityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "activity_logs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String activityId;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
  private User actor;

  @ManyToOne
  @JoinColumn(name = "file_id", referencedColumnName = "fileId")
  private File targetFile;

  @ManyToOne
  @JoinColumn(name = "folder_id", referencedColumnName = "folderId")
  private Folder targetFolder;

  @ManyToOne
  @JoinColumn(name = "bucket_id", referencedColumnName = "bucketId")
  private Bucket targetBucket;

  @Enumerated(EnumType.STRING)
  private ActivityType activityType;

  private String log;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;
}
