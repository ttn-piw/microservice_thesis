package com.thesis.chat_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GuestBookingRequest {
    @NotNull
    String full_name;

    String email;

    @NotNull
    Boolean is_primary;
}
