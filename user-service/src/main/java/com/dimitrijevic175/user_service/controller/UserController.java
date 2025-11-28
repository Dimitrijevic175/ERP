package com.dimitrijevic175.user_service.controller;

import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.dto.CreateUserRequest;
import com.dimitrijevic175.user_service.dto.UserResponse;
import com.dimitrijevic175.user_service.security.CheckSecurity;
import com.dimitrijevic175.user_service.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @CheckSecurity(roles = {"ADMIN"})
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
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
}
