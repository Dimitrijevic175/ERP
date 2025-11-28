package com.dimitrijevic175.user_service.mapper;

import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.dto.UserResponse;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleName(user.getRole().getName().name())
                .active(user.isActive())
                .build();
    }

}
