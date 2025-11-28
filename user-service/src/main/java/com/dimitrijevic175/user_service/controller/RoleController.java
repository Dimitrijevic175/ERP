package com.dimitrijevic175.user_service.controller;

import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.dto.CreateRoleRequest;
import com.dimitrijevic175.user_service.dto.RoleResponse;
import com.dimitrijevic175.user_service.mapper.RoleMapper;
import com.dimitrijevic175.user_service.security.CheckSecurity;
import com.dimitrijevic175.user_service.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // POST /roles – only admin
    @PostMapping
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleRequest request) {
        Role created = roleService.createRole(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(RoleMapper.toResponse(role));
    }
}
