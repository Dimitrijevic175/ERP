package com.dimitrijevic175.user_service.mapper;

import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.dto.RoleResponse;

public class RoleMapper {
    public static RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName().name())
                .build();
    }
}
