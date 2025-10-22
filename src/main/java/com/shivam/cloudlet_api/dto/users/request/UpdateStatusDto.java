package com.shivam.cloudlet_api.dto.users.request;

import com.shivam.cloudlet_api.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {
  private UserStatus status;
}
