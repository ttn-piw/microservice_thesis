package com.thesis.auth_service.repository;

import com.thesis.auth_service.document.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student,String>{
}
