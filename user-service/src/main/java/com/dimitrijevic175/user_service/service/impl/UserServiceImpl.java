package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.configuration.PasswordUtil;
import com.dimitrijevic175.user_service.configuration.UserSpecification;
import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.dto.UserUpdateRequest;
import com.dimitrijevic175.user_service.exceptions.*;
import com.dimitrijevic175.user_service.mapper.UserMapper;
import com.dimitrijevic175.user_service.repository.RoleRepository;
import com.dimitrijevic175.user_service.repository.UserRepository;
import com.dimitrijevic175.user_service.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email '{}'", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Cannot create user: email '{}' already exists", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(RoleName.valueOf(request.getRoleName()))
                .orElseThrow(() -> {
                    log.error("Role not found: {}", request.getRoleName());
                    return new RuntimeException("Role not found: " + request.getRoleName());
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(role);
        user.setActive(true);

        user = userRepository.save(user);
        log.info("User created successfully with ID '{}'", user.getId());

        return UserMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getUsers(String email, String firstName, String lastName, RoleName roleName, Boolean active, Pageable pageable) {
        log.info("Fetching users with filters: email={}, firstName={}, lastName={}, role={}, active={}", email, firstName, lastName, roleName, active);

        Specification<User> spec = Specification.where(UserSpecification.hasEmail(email))
                .and(UserSpecification.hasFirstName(firstName))
                .and(UserSpecification.hasLastName(lastName))
                .and(UserSpecification.hasRole(roleName))
                .and(UserSpecification.isActive(active));

        Page<UserResponse> result = userRepository.findAll(spec, pageable).map(UserMapper::toResponse);
        log.info("Fetched {} users", result.getTotalElements());
        return result;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID '{}'", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID '{}'", id);
                    return new UserNotFoundException(id);
                });
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID '{}'", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with ID '{}'", id);
                    return new UserNotFoundException(id);
                });

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Cannot update user ID '{}': email '{}' already exists", id, request.getEmail());
                throw new EmailAlreadyExistsException(request.getEmail());
            }
            log.info("Updating email for user ID '{}' to '{}'", id, request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            log.info("Updating first name for user ID '{}' to '{}'", id, request.getFirstName());
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            log.info("Updating last name for user ID '{}' to '{}'", id, request.getLastName());
            user.setLastName(request.getLastName());
        }

        if (request.getRoleName() != null) {
            RoleName rn;
            try {
                rn = RoleName.valueOf(request.getRoleName());
            } catch (IllegalArgumentException ex) {
                log.warn("Role '{}' not found during update for user ID '{}'", request.getRoleName(), id);
                throw new RoleNotFoundException(request.getRoleName());
            }

            Role role = roleRepository.findByName(rn)
                    .orElseThrow(() -> {
                        log.warn("Role '{}' not found in repository during update for user ID '{}'", request.getRoleName(), id);
                        return new RoleNotFoundException(request.getRoleName());
                    });
            log.info("Updating role for user ID '{}' to '{}'", id, request.getRoleName());
            user.setRole(role);
        }

        if (request.getActive() != null) {
            log.info("Updating active status for user ID '{}' to '{}'", id, request.getActive());
            user.setActive(request.getActive());
        }

        User updated = userRepository.save(user);
        log.info("User with ID '{}' updated successfully", id);
        return UserMapper.toResponse(updated);
    }

    @Transactional
    public void verifyPassword(Long userId, String oldPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!PasswordUtil.checkPassword(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }

    }

    @Override
    @Transactional
    public UserResponse updatePassword(Long userId, String newPassword) {
        log.info("Updating password for user ID '{}'", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for password update with ID '{}'", userId);
                    return new UserNotFoundException(userId);
                });

        user.setPassword(PasswordUtil.hashPassword(newPassword));
        userRepository.save(user);

        log.info("Password updated successfully for user ID '{}'", userId);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long requesterId) {
        log.info("Deleting user ID '{}' requested by user ID '{}'", id, requesterId);

        if (requesterId.equals(id)) {
            log.warn("User ID '{}' attempted to delete self", id);
            throw new SelfDeletionException();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion with ID '{}'", id);
                    return new UserNotFoundException(id);
                });

        userRepository.delete(user);
        log.info("User ID '{}' deleted successfully by user ID '{}'", id, requesterId);
    }
}
