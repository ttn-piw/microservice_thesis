package com.thesis.chat_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityRoomRequest {
    @JsonProperty(required = true) String roomTypeName;
    @JsonProperty(required = true) String city;

    @Schema(description = "Check-in date in YYYY-MM-DD format", example = "2025-12-01")
    @JsonProperty(required = true) LocalDate checkInDate;

    @Schema(description = "Check-out date in YYYY-MM-DD format", example = "2025-12-01")
    @JsonProperty(required = true) LocalDate checkOutDate;

    @JsonProperty(required = true) int quantity;
}
