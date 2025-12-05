package com.thesis.review_service.repository;

import com.thesis.review_service.document.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {

}
