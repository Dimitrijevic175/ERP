package com.dimitrijevic175.user_service.service.impl;

import com.dimitrijevic175.user_service.configuration.PasswordUtil;
import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.dto.LoginRequest;
import com.dimitrijevic175.user_service.dto.LoginResponse;
import com.dimitrijevic175.user_service.repository.UserRepository;
import com.dimitrijevic175.user_service.security.TokenService;
import com.dimitrijevic175.user_service.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }
    @Override
    public LoginResponse login(LoginRequest request) {
        // Pronađi korisnika po emailu
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Proveri lozinku
        if (!PasswordUtil.checkPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Kreiraj claims za token
        Claims claims = Jwts.claims();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().getName().name());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());

        // Generiši token preko TokenService
        String token = tokenService.generate(claims);

        // Vrati LoginResponse DTO
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName().name()
        );
    }

}
