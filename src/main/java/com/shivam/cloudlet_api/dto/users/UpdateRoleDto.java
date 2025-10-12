package com.shivam.cloudlet_api.dto.users;

import com.shivam.cloudlet_api.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoleDto {
  private UserRole role;
}
