package com.dimitrijevic175.user_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
    private boolean active;
}
