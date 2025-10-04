package com.shivam.cloudlet_api.dto.users;

import com.shivam.cloudlet_api.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
  private String email;
  private UserRole role;
}