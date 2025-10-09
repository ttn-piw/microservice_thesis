package com.thesis.auth_service.repository;

import com.thesis.auth_service.document.Auth;
import jakarta.validation.constraints.Email;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthRepository extends MongoRepository<Auth,String>{
    boolean existsByEmail(String email);

    Auth getAuthByEmail(String email);
}
