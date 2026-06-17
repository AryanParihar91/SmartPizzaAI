package com.smartpizza.authservice.controller;

import com.smartpizza.authservice.dto.AuthResponse;
import com.smartpizza.authservice.dto.LoginRequest;
import com.smartpizza.authservice.dto.RegisterRequest;
import com.smartpizza.authservice.dto.UserResponse;
import com.smartpizza.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //sign up api
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    //sign in api
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    //get user by id
    @GetMapping("/users/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return authService.getUserById(userId);
    }

    @GetMapping("/test")
    public String test() {
        return "Auth service is working";
    }
}