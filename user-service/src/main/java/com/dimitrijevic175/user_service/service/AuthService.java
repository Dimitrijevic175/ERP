package com.dimitrijevic175.user_service.service;

import com.dimitrijevic175.user_service.dto.LoginRequest;
import com.dimitrijevic175.user_service.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
