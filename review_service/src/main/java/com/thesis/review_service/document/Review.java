package com.thesis.review_service.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "review")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @Id
    String id;

    @Field("booking_id")
    String bookingId;

    @Field("user_id")
    String userId;

    @Field("hotel_id")
    String hotelId;

    Integer rating;
    String comment;

    @Field("created_at")
    LocalDateTime createdAt;

    @Field("updated_at")
    LocalDateTime updatedAt;
}
