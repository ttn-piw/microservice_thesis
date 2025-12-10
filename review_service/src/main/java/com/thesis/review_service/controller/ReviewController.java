package com.thesis.review_service.controller;

import com.thesis.review_service.document.Review;
import com.thesis.review_service.dto.request.ReviewRequest;
import com.thesis.review_service.dto.response.ApiResponse;
import com.thesis.review_service.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    Logger log = LoggerFactory.getLogger(ReviewController.class);

    @GetMapping("/test")
    public String testReviewsService(){
        return "This is review service";
    }

    @GetMapping("/all")
    public List<Review> getAllReviews(){
        return reviewService.getAllReviews();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getReviewsByUser(HttpServletRequest request,@PathVariable String userId) {
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse reviews = reviewService.getReviewsByUserId(userId);
        HttpStatus status = reviews.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(reviews);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getReviewsByUserEmail(HttpServletRequest request,
                                                             @RequestParam String email,
                                                             @RequestParam String userId) {
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse reviews = reviewService.getReviewOfMe(email, userId);
        HttpStatus status = reviews.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(reviews);
    }

    @PostMapping("/review")
    public ResponseEntity<ApiResponse> postReview(HttpServletRequest request, @RequestBody ReviewRequest body){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = reviewService.createReview(body);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/owner")
    public ResponseEntity<ApiResponse> getOwnerReview(HttpServletRequest request,
                                                       @RequestParam(required = true) String email){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + email ;
        log.info(path);

        ApiResponse response = reviewService.getAdminOwnerReview(email);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
