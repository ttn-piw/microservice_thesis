package com.thesis.chat_service.service;

import com.thesis.chat_service.dto.request.AvailabilityRoomRequest;
import com.thesis.chat_service.dto.response.ApiResponse;
import com.thesis.chat_service.repository.httpClient.BookingClient;
import com.thesis.chat_service.repository.httpClient.HotelClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingAgentTools {
    private final HotelClient hotelClient;
    private final BookingClient bookingClient;

    @Tool(description = "Checks room availability for dates, city, type, and quantity. Returns SUCCESS or a detailed FAILURE message.")
    public String getAvailability(AvailabilityRoomRequest request) {
        try {
            log.info("CALLING TO getAvailability");
            ApiResponse<List<Map<String, Object>>> hotelResponse = hotelClient.searchForChat(request.getCity(), request.getRoomTypeName());
            List<Map<String, Object>> foundRooms = hotelResponse.getData();

            log.info("Found rooms: {}", foundRooms);
            if (foundRooms == null || foundRooms.isEmpty()) {
                return String.format("FAILURE: No rooms of type '%s' found in '%s'.", request.getRoomTypeName(), request.getCity());
            }

            StringBuilder resultBuilder = new StringBuilder();
            boolean anyAvailable = false;

            for (Map<String, Object> room : foundRooms) {
                UUID roomTypeId = UUID.fromString(room.get("roomTypeId").toString());
                String hotelName = (String) room.get("hotelName");

                if (roomTypeId == null) {
                    log.warn("Skipping room with null ID for hotel: {}", hotelName);
                    continue;
                }

                try {
                    Integer availableCount = bookingClient.checkAvailability(
                            roomTypeId,
                            request.getCheckInDate(),
                            request.getCheckOutDate(),
                            request.getQuantity()
                    );

                    if (availableCount != null && availableCount >= request.getQuantity()) {
                        anyAvailable = true;
                        resultBuilder.append(String.format("- %s: %d rooms available.\n", hotelName, availableCount));
                    }
                } catch (Exception e) {
                    log.error("Error checking availability for roomTypeId {}: {}", roomTypeId, e.getMessage());
                }
            }

            if (anyAvailable) {
                return "SUCCESS: Found available rooms:\n" + resultBuilder.toString() + "Ready for booking.";
            } else {
                return String.format("FAILURE: Found %d hotels with '%s' in '%s', but none have availability for the selected dates.",
                        foundRooms.size(), request.getRoomTypeName(), request.getCity());
            }

        } catch (FeignException e) {
            log.error("Feign Error calling microservices: Status {}", e.status(), e);
            return "ERROR: Unable to connect to hotel or booking services. Please try again later.";
        } catch (Exception e) {
            log.error("Internal Error in getAvailability tool: ", e);
            return "ERROR: An internal error occurred while processing your request.";
        }
    }


}
