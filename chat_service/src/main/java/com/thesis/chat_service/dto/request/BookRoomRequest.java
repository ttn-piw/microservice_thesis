package com.thesis.chat_service.dto.request;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("Details required to book a hotel room")
public class BookRoomRequest {

    @JsonProperty(required = true, value = "roomTypeId")
    private String roomTypeId;

    @JsonProperty(required = true, value = "hotelName")
    private String hotelName;

    @JsonProperty(required = true, value = "checkInDate")
    private String checkInDate;

    @JsonProperty(required = true, value = "checkOutDate")
    private String checkOutDate;

    @JsonProperty(required = true, value = "quantity")
    private int quantity;

    @JsonProperty(required = true, value = "customerName")
    private String customerName;

    @JsonProperty(required = true, value = "customerEmail")
    private String customerEmail;

    @JsonProperty(required = true, value = "hotelId")
    private UUID hotelId;

    @JsonProperty(required = false, value = "specialRequests")
    private String specialRequests;

}