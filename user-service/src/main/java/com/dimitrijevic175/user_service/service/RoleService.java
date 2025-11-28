package com.dimitrijevic175.user_service.service;

import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.dto.CreateRoleRequest;
import com.dimitrijevic175.user_service.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    Role createRole(CreateRoleRequest request);
    Role getRoleById(Long id);
}
