package com.thesis.auth_service.service;

import com.thesis.auth_service.document.Auth;
import com.thesis.auth_service.dto.request.LoginRequest;
import com.thesis.auth_service.dto.request.RegisterRequest;
import com.thesis.auth_service.dto.response.ApiResponse;
import com.thesis.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService {
    @Autowired
    AuthRepository authRepository;

    public List<Auth> getAll() {
        return authRepository.findAll();
    }

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public ApiResponse register (RegisterRequest request){
        if (authRepository.existsByEmail(request.getEmail()))
            return ApiResponse.builder().code(400).message("Email existed").data(null).build();

        if (!request.getPassword().equals(request.getRePassword()))
            return ApiResponse.builder().code(400).message("Password is not matched").data(null).build();

        Auth registerUser = new Auth();
        registerUser.setEmail(request.getEmail());
        registerUser.setUsername(request.getUsername());
        registerUser.setPassword(passwordEncoder.encode(request.getPassword()));
        registerUser.setRoles(Collections.singleton("USER"));
        registerUser.setStatus("ACTIVE");

        //Get current timestamp
        Instant getCurrentTime = Instant.now();
        registerUser.setCreated_at(getCurrentTime.toString());
        registerUser.setUpdated_at(getCurrentTime.toString());

        authRepository.save(registerUser);

        return ApiResponse.builder()
                .code(200)
                .message("Register new account successfully!")
                .data(null)
                .build();
    }
}
