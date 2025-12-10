package com.thesis.review_service.service;

import com.thesis.review_service.document.Review;
import com.thesis.review_service.dto.request.ReviewRequest;
import com.thesis.review_service.dto.response.ApiResponse;
import com.thesis.review_service.dto.response.AvailableReviewBookingResponse;
import com.thesis.review_service.dto.response.BookingResponse;
import com.thesis.review_service.dto.response.ReviewResponse;
import com.thesis.review_service.repository.ReviewRepository;
import com.thesis.review_service.repository.httpClient.BookingClient;
import com.thesis.review_service.repository.httpClient.hotelClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookingClient bookingClient;

    @Autowired
    hotelClient hotelClient;

    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    public ApiResponse createReview(ReviewRequest req) {
        //Check posted review
        UUID bookingId = UUID.fromString(req.getBookingId());
        var bookingResponse = bookingClient.getBookingById(bookingId);

        if (bookingResponse.getData() == null) {
            throw new RuntimeException("Booking not found");
        }

        boolean reviewExists = reviewRepository.existsByBookingId(req.getBookingId());

        if (reviewExists) {
            return ApiResponse.builder()
                    .code(400)
                    .message("You have already reviewed this booking.")
                    .build();
        }

        Review newReview = new Review();
        newReview.setUserId(req.getUserId());
        newReview.setBookingId(req.getBookingId());
        newReview.setHotelId(req.getHotelId());
        newReview.setRating(req.getRating());
        newReview.setComment(req.getComment());
        newReview.setCreatedAt(LocalDateTime.now());
        newReview.setUpdatedAt(LocalDateTime.now());

        Review response = reviewRepository.save(newReview);

        return ApiResponse.builder()
                .code(200)
                .data(response)
                .message("Post new comment")
                .build();
    }

    public ApiResponse getReviewsByUserId(String userId){
        List<Review> response = reviewRepository.findReviewByUserId(userId);

        if (response.isEmpty())
            return ApiResponse.builder()
                    .code(404)
                    .data(null)
                    .message("No review founded.")
                    .build();

        return ApiResponse.builder()
                .code(200)
                .data(response)
                .message("Post new comment")
                .build();
    }

    public ApiResponse getAdminOwnerReview(String email){

        try{
            List<UUID> listHotelId = hotelClient.getHotelIdByOwnerId(email);
            List<Review> reviewsList = new ArrayList<>();

            listHotelId.forEach(id -> {
                log.info("Id: {}", id);
                List<Review> reviews = reviewRepository.findReviewByHotelId(id.toString());
                reviewsList.addAll(reviews);
            });

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESSFUL")
                    .data(reviewsList)
                    .build();

        } catch (FeignException e){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public ApiResponse getReviewOfMe(String email, String userId) {
        try {
            List<BookingResponse> bookingResponses = bookingClient.getBookingByEmailToReview(email);
            List<Review> reviewedBookings = reviewRepository.findReviewByUserId(userId);
            List<UUID> listReviewedBookingId = new ArrayList<>();

            reviewedBookings.forEach(reviewedBooking -> listReviewedBookingId.add(UUID.fromString(reviewedBooking.getBookingId())));

            LocalDate today = LocalDate.now();
            List<AvailableReviewBookingResponse> result = new ArrayList<>();
            Set<UUID> reviewedSet = new HashSet<>(listReviewedBookingId);

            for (BookingResponse booking : bookingResponses){
                boolean isConfirmed = "CONFIRMED".equals(booking.getStatus());
                boolean isCheckOutPast = booking.getCheck_out_date().isBefore(today);
                //Check reviewed with HashSet
                boolean isReviewed = reviewedSet.contains(booking.getId());
                boolean canReview = isConfirmed && isCheckOutPast && !isReviewed;

                AvailableReviewBookingResponse response = AvailableReviewBookingResponse.builder()
                        .bookingResponse(booking)
                        .isReviewed(isReviewed)
                        .canReview(canReview)
                        .build();

                result.add(response);
            }

                return ApiResponse.builder()
                        .code(200)
                        .message("Successfully")
                        .data(result)
                        .build();

        } catch (FeignException e) {
            return ApiResponse.builder()
                    .code(500)
                    .data(null)
                    .message(e.getMessage())
                    .build();
        }
    }
}
