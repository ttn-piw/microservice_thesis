package com.thesis.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    String user_id;

    @NotBlank(message = "Name must not be empty")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name;

    String gender;

    @NotBlank(message = "Phone must not be empty")
    String phone;

    String avatar;

    String birthday;
}

