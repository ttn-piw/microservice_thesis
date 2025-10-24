package com.thesis.auth_service.controller;

import com.nimbusds.jose.JOSEException;
import com.thesis.auth_service.dto.request.IntrospectRequest;
import com.thesis.auth_service.dto.request.LoginRequest;
import com.thesis.auth_service.dto.request.RegisterRequest;
import com.thesis.auth_service.dto.response.ApiResponse;
import com.thesis.auth_service.dto.response.IntrospectResponse;
import com.thesis.auth_service.repository.httpClient.UserClient;
import com.thesis.auth_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    private UserClient userClient;

    @GetMapping("/test")
    public String testApiGateway(){
        return "It's is auth service";
    }

    @GetMapping("/testFeign")
    public String testFeign() {
        return userClient.callUserService();
    }

    Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAuth(){
        if (authService.getAll().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can not retrieve data");

        return ResponseEntity.status(HttpStatus.OK).body(authService.getAll());
    }

    //Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(HttpServletRequest request, @Valid @RequestBody(required = true) RegisterRequest registerRequest){
        String path = request.getMethod() + " " + request.getRequestURI() ;

        log.info(path);

        ApiResponse responseData = authService.register(registerRequest);

        ApiResponse<Object> response = ApiResponse.builder()
                .message(responseData.getMessage())
                .code(responseData.getCode())
                .data(responseData.getData())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (HttpServletRequest request,@Valid @RequestBody(required = true) LoginRequest loginRequest){

        String path = request.getMethod() + " " + request.getRequestURI() ;
        log.info(path);

        ApiResponse responseData = authService.login(loginRequest);

        ApiResponse<Object> response = ApiResponse.builder()
                .message(responseData.getMessage())
                .code(responseData.getCode())
                .data(responseData.getData())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
        throws ParseException, JOSEException {
        var data = authService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(data)
                .build();
    }
}
