package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.dto.CreateRoleRequest;
import com.dimitrijevic175.user_service.dto.RoleResponse;
import com.dimitrijevic175.user_service.exceptions.RoleAlreadyExistsException;
import com.dimitrijevic175.user_service.exceptions.RoleNotFoundException;
import com.dimitrijevic175.user_service.exceptions.UnsupportedRoleException;
import com.dimitrijevic175.user_service.repository.RoleRepository;
import com.dimitrijevic175.user_service.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleResponse(role.getId(), role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Role createRole(CreateRoleRequest request) {
        RoleName rn;
        try {
            rn = RoleName.valueOf(request.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedRoleException(request.getRoleName());
        }

        boolean exists = roleRepository.findByName(rn).isPresent();
        if (exists) {
            throw new RoleAlreadyExistsException(rn.name());
        }

        Role role = new Role();
        role.setName(rn);
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role with id: " + id.toString()));
    }
}
