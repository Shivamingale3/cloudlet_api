package com.shivam.cloudlet_api.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenameFolderDto {
  private String newName;
}
