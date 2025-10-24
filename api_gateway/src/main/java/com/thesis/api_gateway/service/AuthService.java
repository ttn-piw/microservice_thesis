package com.thesis.api_gateway.service;

import com.thesis.api_gateway.dto.request.IntrospectRequest;
import com.thesis.api_gateway.dto.response.ApiResponse;
import com.thesis.api_gateway.dto.response.IntrospectResponse;
import com.thesis.api_gateway.repository.AuthClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @Autowired
    AuthClient authClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return authClient.introspect(IntrospectRequest.builder().token(token).build());
    }
}
