package com.dimitrijevic175.user_service.controller;

import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UpdatePasswordRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.dto.UserUpdateRequest;
import com.dimitrijevic175.user_service.security.CheckSecurity;
import com.dimitrijevic175.user_service.security.TokenService;
import com.dimitrijevic175.user_service.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }
    @CheckSecurity(roles = {"ADMIN"})
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @CheckSecurity(roles = {"ADMIN"})
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) RoleName roleName,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUsers(email, firstName, lastName, roleName, active, pageable));
    }

    @CheckSecurity(roles = {"ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/password")
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<UserResponse> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        return ResponseEntity.ok(userService.updatePassword(id, request.getNewPassword()));
    }

    @DeleteMapping("/{id}")
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        Claims claims = tokenService.parseToken(token);
        Long requesterId = Long.valueOf(claims.getSubject());

        userService.deleteUser(id, requesterId);
        return ResponseEntity.noContent().build();
    }
}
