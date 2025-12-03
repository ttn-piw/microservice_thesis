package com.thesis.chat_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(required = true) LocalDate checkInDate;
    @JsonProperty(required = true) LocalDate checkOutDate;
    @JsonProperty(required = true) int quantity;
}
