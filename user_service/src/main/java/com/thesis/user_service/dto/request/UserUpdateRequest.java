package com.thesis.user_service.dto.request;

import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @NotBlank(message = "Name must not be empty")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name;

    String gender;

    @NotBlank(message = "Phone must not be empty")
    String phone;

    @Nullable
    String avatar;
    String dob;
}

