package com.thesis.review_service.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "review")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    private String id;

    private String userId;
    private String hotelId;

    private Integer rating;
    private String comment;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
