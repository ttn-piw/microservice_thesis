package com.thesis.review_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableReviewBookingResponse {
    BookingResponse bookingResponse;
    boolean canReview;
    boolean isReviewed;
}
