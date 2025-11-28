package com.dimitrijevic175.user_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Long id;
    private String name;
}
