package com.dimitrijevic175.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordRequest {

    @NotBlank
    private String oldPassword;

}
