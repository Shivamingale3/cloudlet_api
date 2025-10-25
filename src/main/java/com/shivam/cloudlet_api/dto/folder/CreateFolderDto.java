package com.shivam.cloudlet_api.dto.folder;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFolderDto {
  private String name;
  @Null
  private String parentId;
}
