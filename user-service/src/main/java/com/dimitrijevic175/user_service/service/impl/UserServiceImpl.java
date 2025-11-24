package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.dto.LoginRequest;
import com.dimitrijevic175.user_service.dto.LoginResponse;
import com.dimitrijevic175.user_service.repository.UserRepository;
import com.dimitrijevic175.user_service.security.TokenService;
import com.dimitrijevic175.user_service.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
