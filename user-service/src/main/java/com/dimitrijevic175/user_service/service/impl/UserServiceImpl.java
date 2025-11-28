package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.configuration.PasswordUtil;
import com.dimitrijevic175.user_service.configuration.UserSpecification;
import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.dto.UserUpdateRequest;
import com.dimitrijevic175.user_service.exceptions.EmailAlreadyExistsException;
import com.dimitrijevic175.user_service.exceptions.RoleNotFoundException;
import com.dimitrijevic175.user_service.exceptions.SelfDeletionException;
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

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));


        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {

            boolean exists = userRepository.existsByEmail(request.getEmail());
            if (exists) {
                throw new EmailAlreadyExistsException(request.getEmail());
            }

            user.setEmail(request.getEmail());
        }


        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }


        if (request.getRoleName() != null) {
            RoleName rn;
            try {
                rn = RoleName.valueOf(request.getRoleName());
            } catch (IllegalArgumentException ex) {
                throw new RoleNotFoundException(request.getRoleName());
            }

            Role role = roleRepository.findByName(rn)
                    .orElseThrow(() -> new RoleNotFoundException(request.getRoleName()));
            user.setRole(role);
        }


        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }


        User updated = userRepository.save(user);
        return UserMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public UserResponse updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setPassword(PasswordUtil.hashPassword(newPassword));

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long requesterId) {
        if (requesterId.equals(id)) {
            throw new SelfDeletionException();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }
}
