package com.thesis.review_service.repository;

import com.thesis.review_service.document.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    boolean existsByBookingId(String id);

    List<Review> findReviewByUserId(String id);

    List<Review> findReviewByHotelId(String id);
}
