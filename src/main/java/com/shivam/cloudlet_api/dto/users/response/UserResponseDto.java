package com.shivam.cloudlet_api.dto.users.response;

import java.util.Date;

import com.shivam.cloudlet_api.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
  private String id;
  private String username;
  private String email;
  private String password;
  private UserRole role;
  private String avatar;
  private Date createdAt;
  private Date updatedAt;
}
