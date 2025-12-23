package com.dimitrijevic175.warehouse_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @Around("@annotation(com.dimitrijevic175.warehouse_service.security.CheckSecurity)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {


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


        String userRole = claims.get("role", String.class);
        if (userRole == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No role in token");
        }


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckSecurity checkSecurity = method.getAnnotation(CheckSecurity.class);

        if (!Arrays.asList(checkSecurity.roles()).contains(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient role");
        }


        return joinPoint.proceed();
    }
}
