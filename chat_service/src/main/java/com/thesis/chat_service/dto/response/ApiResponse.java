package com.thesis.chat_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ApiResponse<T> {
    T data;
}
