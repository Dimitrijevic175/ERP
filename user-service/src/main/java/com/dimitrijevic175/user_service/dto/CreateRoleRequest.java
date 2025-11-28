package com.dimitrijevic175.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "Role name must not be blank")
    private String roleName;
}
