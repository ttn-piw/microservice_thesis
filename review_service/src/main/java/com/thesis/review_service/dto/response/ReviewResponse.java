package com.thesis.review_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    String Id;
    String bookingId;
    String userId;
    String hotelId;
    int rating;
    String comment;
    LocalDate createdAt;
}

