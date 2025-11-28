package com.dimitrijevic175.user_service.service;

import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    Page<UserResponse> getUsers(String email, String firstName, String lastName, RoleName roleName, Boolean active, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse updatePassword(Long userId, String newPassword);
    void deleteUser(Long id, Long requesterId);

}
