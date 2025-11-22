package com.dimitrijevic175.user_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Configuration
public class SecurityAspect {

    private final TokenService tokenService;
    private final HttpServletRequest request;

    public SecurityAspect(TokenService tokenService, HttpServletRequest request) {
        this.tokenService = tokenService;
        this.request = request;
    }

    @Around("@annotation(com.dimitrijevic175.user_service.security.CheckSecurity)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // Dohvati HTTP Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // ukloni "Bearer "

        Claims claims;
        try {
            claims = tokenService.parseToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token claims");
        }

        // Dohvati user role iz claims
        String userRole = claims.get("role", String.class);
        if (userRole == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No role in token");
        }

        // Dohvati anotaciju i proveri da li je role dozvoljena
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckSecurity checkSecurity = method.getAnnotation(CheckSecurity.class);

        if (!Arrays.asList(checkSecurity.roles()).contains(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient role");
        }

        // Sve je u redu, izvrši originalnu metodu
        return joinPoint.proceed();
    }
}
