package com.smartpizza.authservice.service;

import com.smartpizza.authservice.dto.AuthResponse;
import com.smartpizza.authservice.dto.LoginRequest;
import com.smartpizza.authservice.dto.RegisterRequest;
import com.smartpizza.authservice.dto.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getUserById(Long userId);
}