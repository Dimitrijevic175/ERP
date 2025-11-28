package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.configuration.UserSpecification;
import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.exceptions.UserNotFoundException;
import com.dimitrijevic175.user_service.mapper.UserMapper;
import com.dimitrijevic175.user_service.repository.RoleRepository;
import com.dimitrijevic175.user_service.repository.UserRepository;
import com.dimitrijevic175.user_service.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Role role = roleRepository.findByName(RoleName.valueOf(request.getRoleName()))
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoleName()));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(role);
        user.setActive(true);

        user = userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getUsers(String email, String firstName, String lastName, RoleName roleName, Boolean active, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecification.hasEmail(email))
                .and(UserSpecification.hasFirstName(firstName))
                .and(UserSpecification.hasLastName(lastName))
                .and(UserSpecification.hasRole(roleName))
                .and(UserSpecification.isActive(active));

        return userRepository.findAll(spec, pageable).map(UserMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toResponse(user);
    }
}
