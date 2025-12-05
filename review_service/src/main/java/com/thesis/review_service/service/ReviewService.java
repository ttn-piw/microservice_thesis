package com.thesis.review_service.service;

import com.thesis.review_service.document.Review;
import com.thesis.review_service.dto.request.ReviewRequest;
import com.thesis.review_service.dto.response.ApiResponse;
import com.thesis.review_service.dto.response.ReviewResponse;
import com.thesis.review_service.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    public ApiResponse createReview(ReviewRequest req) {

        Review review = Review.builder()
                .userId(req.getUserId())
                .hotelId(req.getHotelId())
                .rating(req.getRating())
                .comment(req.getComment())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        review = reviewRepository.save(review);

        return ApiResponse.builder()
                .code(200)
                .data(review)
                .message("Post new comment")
                .build();
    }
}
