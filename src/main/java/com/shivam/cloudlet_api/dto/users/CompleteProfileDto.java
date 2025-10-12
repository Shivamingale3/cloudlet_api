package com.shivam.cloudlet_api.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteProfileDto {
  private String username;
  private String password;
  private String avatar;
}
