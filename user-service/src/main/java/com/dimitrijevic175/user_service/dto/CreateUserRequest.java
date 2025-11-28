package com.dimitrijevic175.user_service.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String roleName;
}