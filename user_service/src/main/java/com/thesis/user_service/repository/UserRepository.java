package com.thesis.user_service.repository;

import com.thesis.user_service.document.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    @Query(value = "{ '_id': ?0 }")
    Optional<User> getStudentById(ObjectId id);

    User getUserByPhone(String phone);
}
