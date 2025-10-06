package com.thesis.auth_service.service;

import com.thesis.auth_service.document.Auth;
import com.thesis.auth_service.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    AuthRepository authRepository;

    public List<Auth> getAll() {
        return authRepository.findAll();
    }
}
