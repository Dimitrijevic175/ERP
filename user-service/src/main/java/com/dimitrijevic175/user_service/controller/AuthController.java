package com.dimitrijevic175.user_service.controller;

import com.dimitrijevic175.user_service.dto.LoginRequest;
import com.dimitrijevic175.user_service.dto.LoginResponse;
import com.dimitrijevic175.user_service.service.AuthService;
import com.dimitrijevic175.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
