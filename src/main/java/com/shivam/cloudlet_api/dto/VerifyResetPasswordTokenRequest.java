package com.shivam.cloudlet_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResetPasswordTokenRequest {

    private String token;
    private String userId;

}
