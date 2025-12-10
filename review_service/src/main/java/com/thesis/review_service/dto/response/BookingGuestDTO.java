package com.thesis.review_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingGuestDTO {
    String full_name;
    String email;
    Boolean is_primary;
}