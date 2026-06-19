package com.smartpizza.authservice.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpizza.authservice.client.DeliveryPartnerClient;
import com.smartpizza.authservice.dto.AuthResponse;
import com.smartpizza.authservice.dto.DeliveryPartnerRequest;
import com.smartpizza.authservice.dto.LoginRequest;
import com.smartpizza.authservice.dto.RegisterRequest;
import com.smartpizza.authservice.dto.UserResponse;
import com.smartpizza.authservice.entity.Role;
import com.smartpizza.authservice.entity.User;
import com.smartpizza.authservice.exception.ResourceAlreadyExistsException;
import com.smartpizza.authservice.repository.UserRepository;
import com.smartpizza.authservice.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final DeliveryPartnerClient deliveryPartnerClient;

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {

		log.info("Registration request received for email: {}", request.getEmail());

		if (userRepository.existsByEmail(request.getEmail())) {

			log.warn("Registration failed - Email already exists: {}", request.getEmail());

			throw new ResourceAlreadyExistsException("Email already registered");
		}

		if (userRepository.existsByMobileNumber(request.getMobileNumber())) {

			log.warn("Registration failed - Mobile number already exists: {}", request.getMobileNumber());

			throw new ResourceAlreadyExistsException("Mobile number already registered");
		}

		User user = new User();

		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setMobileNumber(request.getMobileNumber());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());
		user.setCreatedAt(LocalDateTime.now());

		User savedUser = userRepository.save(user);

		log.info("User registered successfully with ID: {}", savedUser.getUserId());

		if (savedUser.getRole() == Role.DELIVERY) {

			log.info("Creating delivery partner profile for user ID: {}", savedUser.getUserId());

			DeliveryPartnerRequest partnerRequest = new DeliveryPartnerRequest();

			partnerRequest.setAuthUserId(savedUser.getUserId());
			partnerRequest.setPartnerName(savedUser.getFullName());
			partnerRequest.setMobileNumber(savedUser.getMobileNumber());
			partnerRequest.setEmail(savedUser.getEmail());
			partnerRequest.setCity("Bengaluru");

			deliveryPartnerClient.createDeliveryPartner(partnerRequest);

			log.info("Delivery partner profile created successfully");
		}

		String token = jwtUtil.generateToken(savedUser);

		log.info("JWT token generated for user: {}", savedUser.getEmail());

		AuthResponse response = new AuthResponse();

		response.setToken(token);
		response.setTokenType("Bearer");
		response.setUserId(savedUser.getUserId());
		response.setFullName(savedUser.getFullName());
		response.setEmail(savedUser.getEmail());
		response.setRole(savedUser.getRole());

		return response;
	}

	@Override
	public AuthResponse login(LoginRequest request) {

		log.info("Login attempt for email: {}", request.getEmail());

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		log.info("Authentication successful for email: {}", request.getEmail());

		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {

			log.error("User not found with email: {}", request.getEmail());

			return new RuntimeException("User not found");
		});

		String token = jwtUtil.generateToken(user);

		log.info("JWT token generated successfully for user ID: {}", user.getUserId());

		AuthResponse response = new AuthResponse();

		response.setToken(token);
		response.setTokenType("Bearer");
		response.setUserId(user.getUserId());
		response.setFullName(user.getFullName());
		response.setEmail(user.getEmail());
		response.setRole(user.getRole());

		return response;
	}

	@Override
	public UserResponse getUserById(Long userId) {

		log.info("Fetching user details for user ID: {}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> {

			log.error("User not found with ID: {}", userId);

			return new RuntimeException("User not found");
		});

		UserResponse response = new UserResponse();

		response.setUserId(user.getUserId());
		response.setFullName(user.getFullName());
		response.setEmail(user.getEmail());
		response.setMobileNumber(user.getMobileNumber());
		response.setRole(user.getRole());

		log.info("User details fetched successfully for ID: {}", userId);

		return response;
	}
}