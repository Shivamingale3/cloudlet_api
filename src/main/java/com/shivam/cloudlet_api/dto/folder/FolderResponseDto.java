package com.shivam.cloudlet_api.dto.folder;

import java.time.Instant;

import com.shivam.cloudlet_api.dto.users.response.UserResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderResponseDto {
  private String folderId;
  private String name;
  private String parentId; // Only store parent ID to avoid infinite loop
  private UserResponseDto owner;
  private UserResponseDto lastModifiedBy;
  @Builder.Default
  private Long sizeInBytes = 0L; // total size of all files under this folder
  @Builder.Default
  private Boolean shared = false; // optional sharing flag for later ACLs
  private Instant createdAt;
  private Instant updatedAt;
}
