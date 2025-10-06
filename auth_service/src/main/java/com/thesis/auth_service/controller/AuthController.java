package com.thesis.auth_service.controller;

import com.thesis.auth_service.dto.request.LoginRequest;
import com.thesis.auth_service.dto.response.ApiResponse;
import com.thesis.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAuth(){
        if (authService.getAll().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can not retrieve data");

        return ResponseEntity.status(HttpStatus.OK).body(authService.getAll());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (HttpServletRequest request,@Valid @RequestBody(required = true) LoginRequest loginRequest){

        String path = request.getMethod() + " " + request.getRequestURI() ;
        log.info(path);
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .code(200)
                .message("SUCCESS")
                .data("Hello World")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
