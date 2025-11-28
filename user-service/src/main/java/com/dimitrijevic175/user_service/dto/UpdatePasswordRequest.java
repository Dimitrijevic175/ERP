package com.dimitrijevic175.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
