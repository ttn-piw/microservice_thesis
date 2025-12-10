package com.thesis.review_service.repository;

import com.thesis.review_service.document.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    boolean existsByBookingId(String id);

    List<Review> findReviewByUserId(String id);

    List<Review> findReviewByHotelId(String id);

    @Query(value = "{ 'user_id': ?0 }", fields = "{ 'booking_id': 1, '_id': 0 }")
    List<String> findListBookingIdByUserId(String userId);

}
