package com.thesis.review_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    @NotBlank
    String bookingId;

    @NotBlank
    String userId;

    @NotBlank
    String hotelId;

    @Min(1) @Max(5)
    int rating;

    String comment;
}

